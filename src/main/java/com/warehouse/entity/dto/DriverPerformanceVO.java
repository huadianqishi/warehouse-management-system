package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 司机绩效VO（含关联司机信息 + 图表数据 + 每月趋势）
 *
 * @author AI Assistant
 */
@Data
public class DriverPerformanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ============= 司机基础信息 =============
    private Integer id;
    private Integer driverId;
    private String driverName;
    private String driverPhone;
    private String driverLicense;

    // ============= 绩效汇总字段 =============
    private String month;
    private Integer orderCount;
    private BigDecimal totalProfit;
    private LocalDateTime lastUpdateTime;

    // ============= 图表数据 =============
    /** 最近6个月收益趋势 [{month, profit}] */
    private List<MonthProfit> recentTrend;

    /** 按车型分类的收益占比 [{vehicleCategory, profit}] */
    private List<VehicleProfit> vehicleProfit;

    @Data
    public static class MonthProfit implements Serializable {
        private static final long serialVersionUID = 1L;
        private String month;
        private BigDecimal profit;
        private Integer orderCount;
    }

    @Data
    public static class VehicleProfit implements Serializable {
        private static final long serialVersionUID = 1L;
        private String vehicleCategory;
        private BigDecimal profit;
        private Integer orderCount;
    }
}
