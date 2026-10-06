package com.warehouse.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 司机车辆捆绑DTO，用于指派时选择
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverVehicleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 司机ID */
    private Integer driverId;
    /** 司机姓名 */
    private String driverName;
    /** 司机电话 */
    private String driverPhone;
    /** 驾驶证号 */
    private String driverLicense;

    /** 车辆ID */
    private Integer vehicleId;
    /** 车牌号 */
    private String plateNumber;
    /** 车型 */
    private Integer category;
    /** 车型文本 */
    private String categoryText;
    /** 最大载重（吨） */
    private BigDecimal maxWeight;
    /** 最大容积（方） */
    private BigDecimal maxVolume;
}
