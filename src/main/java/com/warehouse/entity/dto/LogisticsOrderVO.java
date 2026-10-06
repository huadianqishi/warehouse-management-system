package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物流订单视图DTO
 * 用于返回订单详情时附带关联信息（发货人、收货人、司机、车辆、商品明细）
 *
 * @author AI助手
 */
@Data
public class LogisticsOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 订单基础信息
    private Integer id;
    private String orderNo;
    private Integer status;
    private String statusText;
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;
    private BigDecimal profitEstimate;
    private java.time.LocalDateTime estimatedTime;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime actualArrivalTime;
    private java.time.LocalDateTime createTime;

    // 收货信息快照（JSON字符串）
    private String receiverSnapshot;

    // 发货人信息
    private AddressInfo sender;

    // 收货人信息
    private AddressInfo receiver;

    // 司机信息
    private DriverInfo driver;

    // 车辆信息
    private VehicleInfo vehicle;

    // 订单商品明细
    private java.util.List<OrderItemVO> items;

    @Data
    public static class AddressInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private String contactName;
        private String phone;
        private String province;
        private String city;
        private String district;
        private String detailAddress;
    }

    @Data
    public static class DriverInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private String realName;
        private String phone;
        private String driverLicense;
    }

    @Data
    public static class VehicleInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private String plateNumber;
        private Integer category;
        private String categoryText;
    }

    @Data
    public static class OrderItemVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private Integer productId;
        private String productName;
        private String productType;
        private java.math.BigDecimal unitPrice;
        private Integer quantity;
        private String batchNo;
        private BigDecimal subtotal;
        private BigDecimal weight;
        private BigDecimal volume;
    }
}
