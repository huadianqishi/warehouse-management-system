package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.common.Result;
import com.warehouse.entity.LogisticsOrder;
import com.warehouse.entity.OrderFinance;
import com.warehouse.entity.User;
import com.warehouse.entity.Vehicle;
import com.warehouse.entity.dto.FinanceVO;
import com.warehouse.mapper.LogisticsOrderMapper;
import com.warehouse.mapper.UserMapper;
import com.warehouse.mapper.VehicleMapper;
import com.warehouse.service.OrderFinanceService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单费用控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/order-finance")
public class OrderFinanceController {

    @Autowired
    private OrderFinanceService orderFinanceService;

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    /**
     * 创建订单费用记录
     */
    @PostMapping("/create")
    public Result<String> createOrderFinance(@RequestBody OrderFinance finance, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderFinanceService.createOrderFinance(finance, userId);
        return Result.success("创建费用记录成功");
    }

    /**
     * 更新订单费用
     */
    @PutMapping("/update")
    public Result<String> updateOrderFinance(@RequestBody OrderFinance finance, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderFinanceService.updateOrderFinance(finance, userId);
        return Result.success("更新费用成功");
    }

    /**
     * 根据订单ID查询费用
     */
    @GetMapping("/get-by-order/{orderId}")
    public Result<OrderFinance> getFinanceByOrderId(@PathVariable Integer orderId) {
        return Result.success(orderFinanceService.getFinanceByOrderId(orderId));
    }

    /**
     * 提交结算申请（未结 → 待审）
     */
    @PostMapping("/submit-audit/{orderId}")
    public Result<String> submitForAudit(@PathVariable Integer orderId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderFinanceService.submitForAudit(orderId, userId);
        return Result.success("结算申请已提交");
    }

    /**
     * 财务终审确认结算（待审 → 已结）
     */
    @PostMapping("/confirm/{orderId}")
    public Result<String> confirmSettle(@PathVariable Integer orderId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderFinanceService.confirmSettle(orderId, userId);
        return Result.success("结算确认成功");
    }

    /**
     * 查询指定结算状态的订单费用列表（富文本VO，含关联信息）
     * 支持按时间范围筛选、结算状态筛选、订单状态筛选
     * 注意：默认过滤已取消订单（status=4），仅展示有财务意义的订单
     */
    @GetMapping("/list")
    public Result<List<FinanceVO>> getFinances(
            @RequestParam(required = false) Integer settlementStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer orderStatus) {
        QueryWrapper<OrderFinance> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("update_time");
        if (settlementStatus != null) {
            wrapper.eq("settlement_status", settlementStatus);
        }
        // 默认过滤已取消订单（order_status=4）
        if (orderStatus != null) {
            wrapper.eq("order_status", orderStatus);
        } else {
            wrapper.ne("order_status", 4);
        }
        List<OrderFinance> finances = orderFinanceService.getFinancesByStatus(settlementStatus);
        if (finances == null) finances = new ArrayList<>();

        // 收集关联数据
        Set<Integer> orderIds = finances.stream().map(OrderFinance::getOrderId).collect(Collectors.toSet());
        Map<Integer, LogisticsOrder> orderMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            List<LogisticsOrder> orders = logisticsOrderMapper.selectBatchIds(orderIds);
            orderMap = orders.stream().collect(Collectors.toMap(LogisticsOrder::getId, o -> o));
        }

        Set<Integer> driverIds = orderMap.values().stream()
                .map(LogisticsOrder::getDriverId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, User> driverMap = new HashMap<>();
        if (!driverIds.isEmpty()) {
            List<User> drivers = userMapper.selectBatchIds(driverIds);
            driverMap = drivers.stream().collect(Collectors.toMap(User::getId, u -> u));
        }

        Set<Integer> vehicleIds = orderMap.values().stream()
                .map(LogisticsOrder::getVehicleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, Vehicle> vehicleMap = new HashMap<>();
        if (!vehicleIds.isEmpty()) {
            List<Vehicle> vehicles = vehicleMapper.selectBatchIds(vehicleIds);
            vehicleMap = vehicles.stream().collect(Collectors.toMap(Vehicle::getId, v -> v));
        }

        List<FinanceVO> voList = new ArrayList<>();
        for (OrderFinance f : finances) {
            LogisticsOrder order = orderMap.get(f.getOrderId());

            // 默认过滤已取消订单
            if (order != null && order.getStatus() != null && order.getStatus() == 4) {
                continue;
            }
            // 按订单状态精确筛选（如果有传 orderStatus 参数）
            if (orderStatus != null && order != null && order.getStatus() != null
                    && !order.getStatus().equals(orderStatus)) {
                continue;
            }

            FinanceVO vo = new FinanceVO();
            vo.setId(f.getId());
            vo.setOrderId(f.getOrderId());
            vo.setTotalRevenue(f.getTotalRevenue());
            vo.setTotalCost(f.getTotalCost());
            vo.setActualProfit(f.getActualProfit());
            vo.setSettlementStatus(f.getSettlementStatus());
            vo.setSettlementStatusText(getSettlementStatusText(f.getSettlementStatus()));
            vo.setUpdateTime(f.getUpdateTime());

            if (order != null) {
                vo.setOrderStatus(order.getStatus());
                vo.setOrderNo(order.getOrderNo());
                vo.setCreateTime(order.getCreateTime());
                vo.setActualArrivalTime(order.getActualArrivalTime());
                vo.setOrderStatusText(getOrderStatusText(order.getStatus()));

                if (order.getDriverId() != null) {
                    User driver = driverMap.get(order.getDriverId());
                    if (driver != null) {
                        vo.setDriverName(driver.getRealName());
                        vo.setDriverPhone(driver.getPhone());
                    }
                }
                if (order.getVehicleId() != null) {
                    Vehicle vehicle = vehicleMap.get(order.getVehicleId());
                    if (vehicle != null) {
                        vo.setVehiclePlateNumber(vehicle.getPlateNumber());
                    }
                }
            }
            voList.add(vo);
        }

        // 时间范围过滤
        if (startDate != null && !startDate.isEmpty()) {
            voList = voList.stream()
                    .filter(v -> v.getUpdateTime() != null && v.getUpdateTime().toLocalDate().compareTo(LocalDate.parse(startDate)) >= 0)
                    .collect(Collectors.toList());
        }
        if (endDate != null && !endDate.isEmpty()) {
            voList = voList.stream()
                    .filter(v -> v.getUpdateTime() != null && v.getUpdateTime().toLocalDate().compareTo(LocalDate.parse(endDate)) <= 0)
                    .collect(Collectors.toList());
        }

        return Result.success(voList);
    }

    /**
     * 重新汇总费用明细，重新计算利润
     */
    @PostMapping("/recalculate/{financeId}")
    public Result<String> recalculateProfit(@PathVariable Integer financeId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderFinanceService.recalculateProfit(financeId, userId);
        return Result.success("利润重新计算成功");
    }

    /**
     * 计算某订单的实际利润
     */
    @GetMapping("/profit/{orderId}")
    public Result<BigDecimal> calculateProfit(@PathVariable Integer orderId) {
        return Result.success(orderFinanceService.calculateActualProfit(orderId));
    }

    /**
     * 导出费用明细为 Excel
     */
    @GetMapping("/export")
    public void exportFinance(
            @RequestParam(required = false) Integer settlementStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer orderStatus,
            HttpServletResponse response) throws Exception {
        Result<List<FinanceVO>> result = getFinances(settlementStatus, startDate, endDate, orderStatus);
        List<FinanceVO> voList = result.getData();
        if (voList == null) voList = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("费用明细报表");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        String[] headers = {"运单号", "司机", "联系电话", "车牌号", "总营收(元)", "总成本(元)", "实际利润(元)", "结算状态", "更新时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (FinanceVO vo : voList) {
            Row row = sheet.createRow(rowIdx++);
            createCell(row, 0, vo.getOrderNo() != null ? vo.getOrderNo() : "-", dataStyle);
            createCell(row, 1, vo.getDriverName() != null ? vo.getDriverName() : "-", dataStyle);
            createCell(row, 2, vo.getDriverPhone() != null ? vo.getDriverPhone() : "-", dataStyle);
            createCell(row, 3, vo.getVehiclePlateNumber() != null ? vo.getVehiclePlateNumber() : "-", dataStyle);
            createCell(row, 4, vo.getTotalRevenue() != null ? vo.getTotalRevenue().toString() : "0.00", dataStyle);
            createCell(row, 5, vo.getTotalCost() != null ? vo.getTotalCost().toString() : "0.00", dataStyle);
            createCell(row, 6, vo.getActualProfit() != null ? vo.getActualProfit().toString() : "0.00", dataStyle);
            createCell(row, 7, vo.getSettlementStatusText() != null ? vo.getSettlementStatusText() : "-", dataStyle);
            createCell(row, 8, vo.getUpdateTime() != null ? vo.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-", dataStyle);
        }

        // 合计行
        BigDecimal totalRevenue = voList.stream().map(v -> v.getTotalRevenue() != null ? v.getTotalRevenue() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = voList.stream().map(v -> v.getTotalCost() != null ? v.getTotalCost() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = voList.stream().map(v -> v.getActualProfit() != null ? v.getActualProfit() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        Row sumRow = sheet.createRow(rowIdx);
        sumRow.createCell(0).setCellValue("合计");
        sumRow.createCell(4).setCellValue(totalRevenue.toString());
        sumRow.createCell(5).setCellValue(totalCost.toString());
        sumRow.createCell(6).setCellValue(totalProfit.toString());

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "费用明细报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        workbook.close();
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String getSettlementStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "未结";
            case 1: return "待审";
            case 2: return "已结";
            default: return "未知";
        }
    }

    private String getOrderStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待指派";
            case 1: return "已指派";
            case 2: return "运输中";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
