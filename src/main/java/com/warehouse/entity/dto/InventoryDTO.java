package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 库存信息DTO
 * 用于展示当前库存信息
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
public class InventoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Integer productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品类型
     */
    private String productType;

    /**
     * 货架ID
     */
    private Integer shelfId;

    /**
     * 货架编号
     */
    private String shelfNumber;

    /**
     * 层数
     */
    private Integer floorNumber;

    /**
     * 当前库存数量
     */
    private Integer quantity;

    /**
     * 仓库ID
     */
    private Integer warehouseId;

    /**
     * 仓库编号
     */
    private String warehouseNumber;

    /**
     * 商品安全库存
     */
    private Integer safetyStock;

    /**
     * 是否低库存预警（库存低于安全库存）
     */
    private Boolean lowStockWarning;

    /**
     * 总重量（kg）- 该格口所有入库记录的总重量
     */
    private java.math.BigDecimal totalWeight;

    /**
     * 总体积（m³）- 该格口所有入库记录总体积
     */
    private java.math.BigDecimal totalVolume;
}

