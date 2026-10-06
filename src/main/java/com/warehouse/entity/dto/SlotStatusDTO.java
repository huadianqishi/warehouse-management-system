package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 格口状态DTO
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
public class SlotStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 货架ID
     */
    private Integer shelfId;

    /**
     * 货架编号
     */
    private String shelfNumber;

    /**
     * 仓库ID
     */
    private Integer warehouseId;

    /**
     * 层数
     */
    private Integer floorNumber;

    /**
     * 格口状态：OCCUPIED-已占用，EMPTY-空闲
     */
    private String status;

    /**
     * 商品ID（如果已占用）
     */
    private Integer productId;

    /**
     * 商品名称（如果已占用）
     */
    private String productName;

    /**
     * 当前库存数量
     */
    private Integer quantity;
}

