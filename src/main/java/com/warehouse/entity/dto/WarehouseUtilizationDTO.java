package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 仓库利用率DTO
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
public class WarehouseUtilizationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    private Integer warehouseId;

    /**
     * 仓库编号
     */
    private String warehouseNumber;

    /**
     * 格口总数
     */
    private Integer totalSlots;

    /**
     * 已占用格口数
     */
    private Integer occupiedSlots;

    /**
     * 空闲格口数
     */
    private Integer emptySlots;

    /**
     * 格口利用率（百分比）
     */
    private Double utilizationRate;
}

