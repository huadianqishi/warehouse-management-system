package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.entity.*;
import com.warehouse.entity.dto.CreateLogisticsOrderDTO;
import com.warehouse.entity.dto.LogisticsOrderVO;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.*;
import com.warehouse.mapper.DriverPerformanceMapper;
import com.warehouse.mapper.StockInRecordMapper;
import com.warehouse.mapper.StockOutRecordMapper;
import com.warehouse.service.LogisticsOrderService;
import com.warehouse.service.OperationLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物流订单服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class LogisticsOrderServiceImpl implements LogisticsOrderService {

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderFinanceMapper orderFinanceMapper;

    @Autowired
    private DriverPerformanceMapper driverPerformanceMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StockInRecordMapper stockInRecordMapper;

    @Autowired
    private StockOutRecordMapper stockOutRecordMapper;

    @Autowired
    private OperationLogService operationLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成运单号：LO + 时间戳 + 随机6位
     */
    private String generateOrderNo() {
        return "LO" + System.currentTimeMillis()
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /**
     * 根据发货地和收货地判断距离类型，计算预估送达时间
     * 同城：24h，同省：48h，跨省：72h
     * 起点为下单时间（createTime）
     */
    private LocalDateTime calculateEstimatedTime(AddressBook sender, AddressBook receiver) {
        int hours = 72; // 默认跨省
        if (sender.getProvince().equals(receiver.getProvince())) {
            if (sender.getCity().equals(receiver.getCity())) {
                hours = 24; // 同城
            } else {
                hours = 48; // 同省跨市
            }
        }
        return LocalDateTime.now().plusHours(hours);
    }

    /**
     * 根据省市区构建完整地址字符串
     */
    private String buildFullAddress(AddressBook address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null) sb.append(address.getProvince());
        if (address.getCity() != null) sb.append(address.getCity());
        if (address.getDistrict() != null) sb.append(address.getDistrict());
        if (address.getDetailAddress() != null) sb.append(address.getDetailAddress());
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(CreateLogisticsOrderDTO dto, Integer userId) {
        if (dto.getSenderId() == null) {
            throw new BusinessException("发货人不能为空");
        }
        if (dto.getReceiverId() == null) {
            throw new BusinessException("收货人不能为空");
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        // 获取发货人和收货人信息
        AddressBook sender = addressBookMapper.selectById(dto.getSenderId());
        if (sender == null) {
            throw new BusinessException("发货地址不存在");
        }
        AddressBook receiver = addressBookMapper.selectById(dto.getReceiverId());
        if (receiver == null) {
            throw new BusinessException("收货地址不存在");
        }

        // 查询商品信息并汇总
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal profitEstimate = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CreateLogisticsOrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在，ID: " + itemDTO.getProductId());
            }
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new BusinessException("商品数量必须大于0");
            }

            // 查询该商品最近入库记录中的单件重量和体积
            QueryWrapper<StockInRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("product_id", itemDTO.getProductId())
                    .isNotNull("weight")
                    .isNotNull("volume")
                    .orderByDesc("storage_time")
                    .last("LIMIT 1");
            StockInRecord stockIn = stockInRecordMapper.selectOne(wrapper);

            BigDecimal unitWeight = BigDecimal.ZERO;
            BigDecimal unitVolume = BigDecimal.ZERO;
            if (stockIn != null && stockIn.getWeight() != null && stockIn.getVolume() != null) {
                unitWeight = stockIn.getWeight();
                unitVolume = stockIn.getVolume();
            } else {
                throw new BusinessException("商品[" + product.getName() + "]缺少重量体积信息，请先完成入库");
            }

            totalWeight = totalWeight.add(unitWeight.multiply(new BigDecimal(itemDTO.getQuantity())));
            totalVolume = totalVolume.add(unitVolume.multiply(new BigDecimal(itemDTO.getQuantity())));
            profitEstimate = profitEstimate.add(product.getUnitPrice().multiply(new BigDecimal(itemDTO.getQuantity())));

            OrderDetail detail = new OrderDetail();
            detail.setProductId(itemDTO.getProductId());
            detail.setQuantity(itemDTO.getQuantity());
            detail.setBatchNo(itemDTO.getBatchNo());
            orderDetails.add(detail);
        }

        // 计算预估送达时间
        LocalDateTime estimatedTime = calculateEstimatedTime(sender, receiver);

        // 构建收货信息快照（JSON）
        LogisticsOrderVO.AddressInfo receiverSnapshotVO = new LogisticsOrderVO.AddressInfo();
        receiverSnapshotVO.setId(receiver.getId());
        receiverSnapshotVO.setContactName(receiver.getContactName());
        receiverSnapshotVO.setPhone(receiver.getPhone());
        receiverSnapshotVO.setProvince(receiver.getProvince());
        receiverSnapshotVO.setCity(receiver.getCity());
        receiverSnapshotVO.setDistrict(receiver.getDistrict());
        receiverSnapshotVO.setDetailAddress(receiver.getDetailAddress());
        String receiverSnapshot;
        try {
            receiverSnapshot = objectMapper.writeValueAsString(receiverSnapshotVO);
        } catch (Exception e) {
            receiverSnapshot = buildFullAddress(receiver);
        }

        // 创建订单
        LogisticsOrder order = new LogisticsOrder();
        order.setOrderNo(generateOrderNo());
        order.setStatus(0);
        order.setSenderId(dto.getSenderId());
        order.setReceiverId(dto.getReceiverId());
        order.setReceiverSnapshot(receiverSnapshot);
        order.setTotalWeight(totalWeight);
        order.setTotalVolume(totalVolume);
        order.setEstimatedTime(estimatedTime);
        order.setProfitEstimate(profitEstimate);
        order.setCreateTime(LocalDateTime.now());

        int result = logisticsOrderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException("创建订单失败");
        }

        // 批量保存订单商品明细
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(order.getId());
            orderDetailMapper.insert(detail);
        }

        // 记录操作日志（操作类型ID=23：创建物流订单）
        operationLogService.logOperation(userId, 23, "成功", "logistics_order", order.getId(),
                "创建物流订单：" + order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Integer orderId, Integer userId) {
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == 3) {
            throw new BusinessException("已完成订单无法取消");
        }
        if (order.getStatus() == 4) {
            throw new BusinessException("订单已取消");
        }

        // 状态1（已指派/待装货）：需要释放预占库存
        if (order.getStatus() == 1) {
            QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
            detailWrapper.eq("order_id", orderId);
            List<OrderDetail> details = orderDetailMapper.selectList(detailWrapper);
            for (OrderDetail detail : details) {
                if (detail.getStockRecordId() != null) {
                    stockInRecordMapper.unlockStock(detail.getStockRecordId(), detail.getQuantity());
                }
            }
        }

        if (order.getStatus() == 1 || order.getStatus() == 2) {
            if (order.getVehicleId() != null) {
                Vehicle vehicle = vehicleMapper.selectById(order.getVehicleId());
                if (vehicle != null) {
                    vehicle.setStatus(0);
                    vehicleMapper.updateById(vehicle);
                }
            }
        }
        order.setStatus(4);
        logisticsOrderMapper.updateById(order);
        // 记录操作日志（操作类型ID=27：取消物流订单）
        operationLogService.logOperation(userId, 27, "成功", "logistics_order", orderId,
                "取消物流订单：" + order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignDriverAndVehicle(Integer orderId, Integer driverId, Integer vehicleId, Integer userId) {
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不是待指派，无法指派");
        }

        // 校验司机身份
        User driver = userMapper.selectById(driverId);
        if (driver == null) {
            throw new BusinessException("司机不存在");
        }
        if (driver.getRole() == null || driver.getRole() != 3) {
            throw new BusinessException("该用户不是司机角色");
        }
        if (driver.getDriverLicense() == null || driver.getDriverLicense().trim().isEmpty()) {
            throw new BusinessException("该司机未录入驾驶证信息");
        }

        // 校验车辆
        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle == null) {
            throw new BusinessException("车辆不存在");
        }
        if (vehicle.getStatus() != 0) {
            throw new BusinessException("车辆状态不是空闲，无法指派");
        }
        if (vehicle.getMaxWeight().compareTo(order.getTotalWeight()) < 0) {
            throw new BusinessException("车辆载重不足，当前订单总重量：" + order.getTotalWeight() + " 吨");
        }
        if (vehicle.getMaxVolume().compareTo(order.getTotalVolume()) < 0) {
            throw new BusinessException("车辆容积不足，当前订单总体积：" + order.getTotalVolume() + " 立方");
        }

        // ---------- 预占库存（指派时锁定） ----------
        QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("order_id", orderId);
        List<OrderDetail> details = orderDetailMapper.selectList(detailWrapper);

        for (OrderDetail detail : details) {
            // 优先从指定库位锁定（FIFO：先进先出）
            Map<String, Object> slot = null;
            if (detail.getShelfId() != null && detail.getFloorNumber() != null) {
                slot = stockInRecordMapper.findStockSlotFIFO(
                    detail.getProductId(), detail.getShelfId(), detail.getFloorNumber(), detail.getQuantity());
            }
            // 如果没有指定库位或指定库位不足，则从全库找一个可用的
            if (slot == null) {
                slot = stockInRecordMapper.findFirstAvailableStock(detail.getProductId(), detail.getQuantity());
            }
            if (slot == null) {
                throw new BusinessException("商品ID=" + detail.getProductId() + " 库存不足，无法指派");
            }

            Integer recordId = ((Number) slot.get("id")).intValue();
            stockInRecordMapper.lockStock(recordId, detail.getQuantity());
            detail.setStockRecordId(recordId);
            detail.setShelfId(((Number) slot.get("shelf_id")).intValue());
            detail.setFloorNumber(((Number) slot.get("floor_number")).intValue());
            orderDetailMapper.updateById(detail);
        }

        // 指派车辆 & 状态流转
        order.setDriverId(driverId);
        order.setVehicleId(vehicleId);
        order.setStatus(1);
        logisticsOrderMapper.updateById(order);

        vehicle.setStatus(1);
        vehicleMapper.updateById(vehicle);

        // 记录操作日志（操作类型ID=24：指派物流）
        operationLogService.logOperation(userId, 24, "成功", "logistics_order", orderId,
                "指派物流：运单号：" + order.getOrderNo() + "，司机：" + driver.getRealName() + "，车辆：" + vehicle.getPlateNumber());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startDelivery(Integer orderId, Integer userId) {
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不是已指派，无法开始运输");
        }

        // ---------- 扣减库存 + 生成出库记录 ----------
        QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("order_id", orderId);
        List<OrderDetail> details = orderDetailMapper.selectList(detailWrapper);

        for (OrderDetail detail : details) {
            if (detail.getStockRecordId() == null || detail.getShelfId() == null || detail.getFloorNumber() == null) {
                throw new BusinessException("订单明细缺少库位信息，请重新指派");
            }

            // 扣减实际库存（减 quantity）
            stockInRecordMapper.decreaseQuantity(detail.getStockRecordId(), detail.getQuantity());
            // 释放预占（预占数清零）
            stockInRecordMapper.unlockStock(detail.getStockRecordId(), detail.getQuantity());

            // 生成出库记录
            StockOutRecord stockOut = new StockOutRecord();
            stockOut.setProductId(detail.getProductId());
            stockOut.setQuantity(detail.getQuantity());
            stockOut.setShelfId(detail.getShelfId());
            stockOut.setFloorNumber(detail.getFloorNumber());
            stockOut.setWeight(detail.getWeight());
            stockOut.setVolume(detail.getVolume());
            stockOut.setOutTime(new Date());
            stockOut.setOperatorId(userId);
            stockOut.setDestination(order.getReceiverSnapshot());
            stockOut.setCreatedAt(new Date());
            stockOutRecordMapper.insert(stockOut);
        }

        order.setStatus(2);
        order.setStartTime(LocalDateTime.now());
        logisticsOrderMapper.updateById(order);

        // 同步更新车辆状态为运输中
        if (order.getVehicleId() != null) {
            Vehicle vehicle = vehicleMapper.selectById(order.getVehicleId());
            if (vehicle != null) {
                vehicle.setStatus(1); // 1=运输中
                vehicleMapper.updateById(vehicle);
            }
        }

        // 记录操作日志（操作类型ID=25：开始运输）
        operationLogService.logOperation(userId, 25, "成功", "logistics_order", orderId,
                "开始运输：运单号：" + order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Integer orderId, Integer userId) {
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("订单状态不是运输中，无法确认送达");
        }

        LocalDateTime now = LocalDateTime.now();
        order.setStatus(3);
        order.setActualArrivalTime(now);
        logisticsOrderMapper.updateById(order);

        // 释放车辆：恢复为空闲
        if (order.getVehicleId() != null) {
            Vehicle vehicle = vehicleMapper.selectById(order.getVehicleId());
            if (vehicle != null) {
                vehicle.setStatus(0);
                vehicleMapper.updateById(vehicle);
            }
        }

        // 自动创建财务记录（初始状态：未结）
        OrderFinance finance = new OrderFinance();
        finance.setOrderId(orderId);
        finance.setTotalRevenue(order.getProfitEstimate()); // 预估利润即营收
        finance.setTotalCost(BigDecimal.ZERO);
        finance.setActualProfit(order.getProfitEstimate());
        finance.setSettlementStatus(0); // 0-未结
        finance.setUpdateTime(now);
        orderFinanceMapper.insert(finance);

        // 更新司机绩效
        if (order.getDriverId() != null) {
            updateDriverPerformance(order.getDriverId(), orderId, now);
        }

        operationLogService.logOperation(userId, 26, "成功", "logistics_order", orderId,
                "完成物流订单：" + order.getOrderNo());
    }

    /**
     * 更新司机月度绩效（若当月记录不存在则新建）
     */
    private void updateDriverPerformance(Integer driverId, Integer orderId, LocalDateTime completionTime) {
        String month = completionTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        QueryWrapper<DriverPerformance> wrapper = new QueryWrapper<>();
        wrapper.eq("driver_id", driverId).eq("month", month);
        DriverPerformance perf = driverPerformanceMapper.selectOne(wrapper);

        // 获取订单的实际获利
        QueryWrapper<OrderFinance> financeWrapper = new QueryWrapper<>();
        financeWrapper.eq("order_id", orderId);
        OrderFinance finance = orderFinanceMapper.selectOne(financeWrapper);
        BigDecimal profit = finance != null ? finance.getActualProfit() : BigDecimal.ZERO;

        if (perf == null) {
            perf = new DriverPerformance();
            perf.setDriverId(driverId);
            perf.setMonth(month);
            perf.setOrderCount(1);
            perf.setTotalProfit(profit);
            perf.setLastUpdateTime(LocalDateTime.now());
            driverPerformanceMapper.insert(perf);
        } else {
            perf.setOrderCount(perf.getOrderCount() + 1);
            perf.setTotalProfit(perf.getTotalProfit().add(profit));
            perf.setLastUpdateTime(LocalDateTime.now());
            driverPerformanceMapper.updateById(perf);
        }
    }

    @Override
    public LogisticsOrder getOrderById(Integer id) {
        LogisticsOrder order = logisticsOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    @Override
    public List<LogisticsOrder> getAllOrders(Integer status) {
        QueryWrapper<LogisticsOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        if (status != null) {
            wrapper.eq("status", status);
        }
        return logisticsOrderMapper.selectList(wrapper);
    }

    @Override
    public LogisticsOrder getOrderByNo(String orderNo) {
        QueryWrapper<LogisticsOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        return logisticsOrderMapper.selectOne(wrapper);
    }

    @Override
    public LogisticsOrderVO getOrderVO(Integer orderId) {
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return buildOrderVO(order);
    }

    @Override
    public List<LogisticsOrderVO> getAllOrderVOs(Integer status) {
        List<LogisticsOrder> orders = getAllOrders(status);
        return orders.stream().map(this::buildOrderVO).collect(Collectors.toList());
    }

    /**
     * 将 LogisticsOrder 实体构建为带关联信息的 LogisticsOrderVO
     */
    private LogisticsOrderVO buildOrderVO(LogisticsOrder order) {
        LogisticsOrderVO vo = new LogisticsOrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusText(getStatusText(order.getStatus()));

        // 发货人
        if (order.getSenderId() != null) {
            AddressBook sender = addressBookMapper.selectById(order.getSenderId());
            if (sender != null) {
                LogisticsOrderVO.AddressInfo senderInfo = new LogisticsOrderVO.AddressInfo();
                senderInfo.setId(sender.getId());
                senderInfo.setContactName(sender.getContactName());
                senderInfo.setPhone(sender.getPhone());
                senderInfo.setProvince(sender.getProvince());
                senderInfo.setCity(sender.getCity());
                senderInfo.setDistrict(sender.getDistrict());
                senderInfo.setDetailAddress(sender.getDetailAddress());
                vo.setSender(senderInfo);
            }
        }

        // 收货人
        if (order.getReceiverId() != null) {
            AddressBook receiver = addressBookMapper.selectById(order.getReceiverId());
            if (receiver != null) {
                LogisticsOrderVO.AddressInfo receiverInfo = new LogisticsOrderVO.AddressInfo();
                receiverInfo.setId(receiver.getId());
                receiverInfo.setContactName(receiver.getContactName());
                receiverInfo.setPhone(receiver.getPhone());
                receiverInfo.setProvince(receiver.getProvince());
                receiverInfo.setCity(receiver.getCity());
                receiverInfo.setDistrict(receiver.getDistrict());
                receiverInfo.setDetailAddress(receiver.getDetailAddress());
                vo.setReceiver(receiverInfo);
            }
        }

        // 司机
        if (order.getDriverId() != null) {
            User driver = userMapper.selectById(order.getDriverId());
            if (driver != null) {
                LogisticsOrderVO.DriverInfo driverInfo = new LogisticsOrderVO.DriverInfo();
                driverInfo.setId(driver.getId());
                driverInfo.setRealName(driver.getRealName());
                driverInfo.setPhone(driver.getPhone());
                driverInfo.setDriverLicense(driver.getDriverLicense());
                vo.setDriver(driverInfo);
            }
        }

        // 车辆
        if (order.getVehicleId() != null) {
            Vehicle vehicle = vehicleMapper.selectById(order.getVehicleId());
            if (vehicle != null) {
                LogisticsOrderVO.VehicleInfo vehicleInfo = new LogisticsOrderVO.VehicleInfo();
                vehicleInfo.setId(vehicle.getId());
                vehicleInfo.setPlateNumber(vehicle.getPlateNumber());
                vehicleInfo.setCategory(vehicle.getCategory());
                vehicleInfo.setCategoryText(getVehicleCategoryText(vehicle.getCategory()));
                vo.setVehicle(vehicleInfo);
            }
        }

        // 订单商品明细
        QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("order_id", order.getId());
        List<OrderDetail> details = orderDetailMapper.selectList(detailWrapper);
        List<LogisticsOrderVO.OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderDetail detail : details) {
            LogisticsOrderVO.OrderItemVO itemVO = new LogisticsOrderVO.OrderItemVO();
            itemVO.setId(detail.getId());
            itemVO.setProductId(detail.getProductId());
            itemVO.setQuantity(detail.getQuantity());
            itemVO.setBatchNo(detail.getBatchNo());

            Product product = productMapper.selectById(detail.getProductId());
            if (product != null) {
                itemVO.setProductName(product.getName());
                itemVO.setProductType(product.getType());
                itemVO.setUnitPrice(product.getUnitPrice());
                itemVO.setSubtotal(product.getUnitPrice().multiply(new BigDecimal(detail.getQuantity())));
            }
            itemVOs.add(itemVO);
        }
        vo.setItems(itemVOs);

        return vo;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待指派";
            case 1: return "已指派/待装货";
            case 2: return "运输中";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    private String getVehicleCategoryText(Integer category) {
        if (category == null) return "未知";
        switch (category) {
            case 0: return "微面";
            case 1: return "小货";
            case 2: return "中货";
            case 3: return "大货";
            default: return "未知";
        }
    }
}
