package com.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.common.Result;
import com.warehouse.entity.DriverPerformance;
import com.warehouse.entity.LogisticsOrder;
import com.warehouse.entity.OrderFinance;
import com.warehouse.entity.User;
import com.warehouse.entity.dto.DriverPerformanceVO;
import com.warehouse.mapper.DriverPerformanceMapper;
import com.warehouse.mapper.LogisticsOrderMapper;
import com.warehouse.mapper.OrderFinanceMapper;
import com.warehouse.mapper.UserMapper;
import com.warehouse.service.DriverPerformanceService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
 * 司机绩效控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/driver-performance")
public class DriverPerformanceController {

    @Autowired
    private DriverPerformanceService driverPerformanceService;

    @Autowired
    private DriverPerformanceMapper driverPerformanceMapper;

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    @Autowired
    private OrderFinanceMapper orderFinanceMapper;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/record")
    public Result<String> recordOrderCompletion(@RequestParam Integer driverId,
                                                @RequestParam Integer orderId,
                                                HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        driverPerformanceService.recordOrderCompletion(driverId, orderId, userId);
        return Result.success("记录绩效成功");
    }

    /**
     * 按月份查询绩效汇总列表（含司机姓名/电话/驾驶证）
     */
    @GetMapping("/list-by-month")
    public Result<List<DriverPerformanceVO>> getPerformanceByMonth(@RequestParam(required = false) String month) {
        List<DriverPerformance> list;
        if (month != null && !month.isEmpty()) {
            list = driverPerformanceService.getPerformanceByMonth(month);
        } else {
            QueryWrapper<DriverPerformance> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("month");
            list = driverPerformanceMapper.selectList(wrapper);
        }
        // 填充司机信息
        Set<Integer> driverIds = list.stream().map(DriverPerformance::getDriverId).collect(Collectors.toSet());
        Map<Integer, User> driverMap = new HashMap<>();
        if (!driverIds.isEmpty()) {
            List<User> drivers = userMapper.selectBatchIds(driverIds);
            driverMap = drivers.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        List<DriverPerformanceVO> voList = new ArrayList<>();
        for (DriverPerformance p : list) {
            DriverPerformanceVO vo = new DriverPerformanceVO();
            vo.setId(p.getId());
            vo.setDriverId(p.getDriverId());
            vo.setMonth(p.getMonth());
            vo.setOrderCount(p.getOrderCount());
            vo.setTotalProfit(p.getTotalProfit());
            vo.setLastUpdateTime(p.getLastUpdateTime());
            User driver = driverMap.get(p.getDriverId());
            if (driver != null) {
                vo.setDriverName(driver.getRealName());
                vo.setDriverPhone(driver.getPhone());
                vo.setDriverLicense(driver.getDriverLicense());
            }
            voList.add(vo);
        }
        return Result.success(voList);
    }

    /**
     * 获取某司机的全部历史绩效（含图表数据）
     * 用于司机个人详情页
     */
    @GetMapping("/detail/{driverId}")
    public Result<Map<String, Object>> getDriverDetail(@PathVariable Integer driverId,
                                                       @RequestParam(required = false) String month) {
        Map<String, Object> result = new HashMap<>();

        // 1. 基础信息
        User driver = userMapper.selectById(driverId);
        if (driver == null) {
            return Result.error("司机不存在");
        }
        Map<String, Object> driverInfo = new HashMap<>();
        driverInfo.put("id", driver.getId());
        driverInfo.put("name", driver.getRealName());
        driverInfo.put("phone", driver.getPhone());
        driverInfo.put("license", driver.getDriverLicense());
        result.put("driver", driverInfo);

        // 2. 绩效列表
        List<DriverPerformance> perfList = driverPerformanceService.getPerformanceByDriverId(driverId);
        List<DriverPerformanceVO> voList = new ArrayList<>();
        for (DriverPerformance p : perfList) {
            DriverPerformanceVO vo = new DriverPerformanceVO();
            vo.setId(p.getId());
            vo.setMonth(p.getMonth());
            vo.setOrderCount(p.getOrderCount());
            vo.setTotalProfit(p.getTotalProfit());
            vo.setLastUpdateTime(p.getLastUpdateTime());
            vo.setDriverId(driverId);
            vo.setDriverName(driver.getRealName());
            vo.setDriverPhone(driver.getPhone());
            vo.setDriverLicense(driver.getDriverLicense());
            voList.add(vo);
        }
        result.put("performanceList", voList);

        // 3. 最近6个月趋势数据
        List<DriverPerformanceVO.MonthProfit> trend = new ArrayList<>();
        QueryWrapper<DriverPerformance> trendWrapper = new QueryWrapper<>();
        trendWrapper.eq("driver_id", driverId).orderByAsc("month").last("LIMIT 6");
        List<DriverPerformance> trendList = driverPerformanceMapper.selectList(trendWrapper);
        for (DriverPerformance p : trendList) {
            DriverPerformanceVO.MonthProfit mp = new DriverPerformanceVO.MonthProfit();
            mp.setMonth(p.getMonth());
            mp.setProfit(p.getTotalProfit());
            mp.setOrderCount(p.getOrderCount());
            trend.add(mp);
        }
        result.put("recentTrend", trend);

        // 4. 按车型分类的收益（关联 vehicle + order_finance，取实际利润）
        List<DriverPerformanceVO.VehicleProfit> vehicleProfit = new ArrayList<>();
        List<Map<String, Object>> vehicleStats = logisticsOrderMapper.selectVehicleProfitGroupByDriver(driverId);
        Map<Integer, BigDecimal> profitMap = new LinkedHashMap<>();
        Map<Integer, Integer> countMap = new LinkedHashMap<>();
        for (int cat = 0; cat <= 3; cat++) {
            profitMap.put(cat, BigDecimal.ZERO);
            countMap.put(cat, 0);
        }
        for (Map<String, Object> stat : vehicleStats) {
            Integer cat = stat.get("category") != null ? ((Number) stat.get("category")).intValue() : -1;
            profitMap.put(cat, stat.get("total_profit") != null ? new BigDecimal(stat.get("total_profit").toString()) : BigDecimal.ZERO);
            countMap.put(cat, stat.get("order_count") != null ? ((Number) stat.get("order_count")).intValue() : 0);
        }
        for (int cat = 0; cat <= 3; cat++) {
            DriverPerformanceVO.VehicleProfit vp = new DriverPerformanceVO.VehicleProfit();
            vp.setVehicleCategory(getVehicleCategoryText(cat));
            vp.setProfit(profitMap.getOrDefault(cat, BigDecimal.ZERO));
            vp.setOrderCount(countMap.getOrDefault(cat, 0));
            vehicleProfit.add(vp);
        }
        result.put("vehicleProfit", vehicleProfit);

        // 5. 本月订单明细
        if (month != null && !month.isEmpty()) {
            String startDate = month + "-01";
            String endDate = month + "-31";
            List<LogisticsOrder> monthOrdersList = logisticsOrderMapper.selectList(
                    new QueryWrapper<LogisticsOrder>()
                            .eq("driver_id", driverId)
                            .eq("status", 3)
                            .ge("actual_arrival_time", startDate)
                            .le("actual_arrival_time", endDate)
                            .orderByDesc("actual_arrival_time")
            );
            List<Map<String, Object>> orderDetails = new ArrayList<>();
            for (LogisticsOrder order : monthOrdersList) {
                Map<String, Object> od = new HashMap<>();
                od.put("orderNo", order.getOrderNo());
                od.put("actualArrivalTime", order.getActualArrivalTime());
                od.put("profitEstimate", order.getProfitEstimate());
                OrderFinance finance = orderFinanceMapper.selectOne(
                        new QueryWrapper<OrderFinance>().eq("order_id", order.getId())
                );
                od.put("actualProfit", finance != null ? finance.getActualProfit() : BigDecimal.ZERO);
                orderDetails.add(od);
            }
            result.put("monthOrders", orderDetails);
        }

        return Result.success(result);
    }

    /**
     * 获取所有司机列表（仅 role=3 的用户）
     */
    @GetMapping("/drivers")
    public Result<List<Map<String, Object>>> getAllDrivers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role", 3);
        List<User> drivers = userMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User d : drivers) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("name", d.getRealName());
            m.put("phone", d.getPhone());
            m.put("license", d.getDriverLicense());
            result.add(m);
        }
        return Result.success(result);
    }

    /**
     * 导出司机绩效报表为 Excel
     */
    @GetMapping("/export")
    public void exportPerformance(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer driverId,
            HttpServletResponse response) throws Exception {
        List<DriverPerformance> list;
        if (month != null && !month.isEmpty()) {
            list = driverPerformanceService.getPerformanceByMonth(month);
        } else {
            QueryWrapper<DriverPerformance> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("month");
            if (driverId != null) {
                wrapper.eq("driver_id", driverId);
            }
            list = driverPerformanceMapper.selectList(wrapper);
        }

        // 填充司机信息
        Set<Integer> driverIds = list.stream().map(DriverPerformance::getDriverId).collect(Collectors.toSet());
        Map<Integer, User> driverMap = new HashMap<>();
        if (!driverIds.isEmpty()) {
            List<User> drivers = userMapper.selectBatchIds(driverIds);
            driverMap = drivers.stream().collect(Collectors.toMap(User::getId, u -> u));
        }

        // 构建 Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("司机绩效报表");

        // 表头样式
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

        // 数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        String[] headers = {"司机姓名", "联系电话", "驾驶证号", "统计月份", "完成单量", "创造收益(元)", "最后更新时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (DriverPerformance p : list) {
            Row row = sheet.createRow(rowIdx++);
            User driver = driverMap.get(p.getDriverId());

            createCell(row, 0, driver != null ? driver.getRealName() : "未知", dataStyle);
            createCell(row, 1, driver != null ? driver.getPhone() : "-", dataStyle);
            createCell(row, 2, driver != null ? driver.getDriverLicense() : "-", dataStyle);
            createCell(row, 3, p.getMonth(), dataStyle);
            createCell(row, 4, p.getOrderCount() != null ? p.getOrderCount().toString() : "0", dataStyle);
            createCell(row, 5, p.getTotalProfit() != null ? p.getTotalProfit().toString() : "0.00", dataStyle);
            createCell(row, 6, p.getLastUpdateTime() != null ? p.getLastUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-", dataStyle);
        }

        // 自动列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "司机绩效报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
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
