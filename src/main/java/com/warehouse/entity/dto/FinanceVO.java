package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 费用总览VO（订单费用主表 + 关联物流订单信息）
 *
 * @author AI Assistant
 */
@Data
public class FinanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ============= 费用主表字段 =============
    private Integer id;
    private Integer orderId;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal actualProfit;
    private Integer settlementStatus;
    private String settlementStatusText;
    private LocalDateTime updateTime;

    // ============= 物流订单关联字段 =============
    private String orderNo;
    private String driverName;
    private String driverPhone;
    private String vehiclePlateNumber;
    private String orderStatusText;
    private Integer orderStatus;
    private LocalDateTime createTime;
    private LocalDateTime actualArrivalTime;
}
