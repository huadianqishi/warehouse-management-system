package com.warehouse.service;

import com.warehouse.entity.LogisticsOrder;
import com.warehouse.entity.dto.CreateLogisticsOrderDTO;
import com.warehouse.entity.dto.LogisticsOrderVO;

import java.util.List;

/**
 * 物流订单服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface LogisticsOrderService {

    /**
     * 创建物流订单（自动根据商品明细计算重量体积货值）
     */
    void createOrder(CreateLogisticsOrderDTO dto, Integer userId);

    /**
     * 取消物流订单
     */
    void cancelOrder(Integer orderId, Integer userId);

    /**
     * 指派司机和车辆
     */
    void assignDriverAndVehicle(Integer orderId, Integer driverId, Integer vehicleId, Integer userId);

    /**
     * 标记出发（开始运输）
     */
    void startDelivery(Integer orderId, Integer userId);

    /**
     * 确认送达（完成订单，自动创建财务记录、更新司机绩效）
     */
    void completeOrder(Integer orderId, Integer userId);

    /**
     * 根据ID查询订单
     */
    LogisticsOrder getOrderById(Integer id);

    /**
     * 查询所有订单（支持按状态筛选）
     */
    List<LogisticsOrder> getAllOrders(Integer status);

    /**
     * 根据运单号查询
     */
    LogisticsOrder getOrderByNo(String orderNo);

    /**
     * 获取订单完整视图（含关联信息）
     */
    LogisticsOrderVO getOrderVO(Integer orderId);

    /**
     * 获取订单完整视图列表（含关联信息）
     */
    List<LogisticsOrderVO> getAllOrderVOs(Integer status);
}
