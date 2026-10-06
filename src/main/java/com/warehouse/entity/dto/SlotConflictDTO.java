package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 格口冲突检测DTO
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
public class SlotConflictDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否有冲突
     */
    private Boolean hasConflict;

    /**
     * 冲突的商品ID（如果已占用）
     */
    private Integer conflictProductId;

    /**
     * 冲突的商品名称（如果已占用）
     */
    private String conflictProductName;

    /**
     * 当前库存数量
     */
    private Integer currentQuantity;

    /**
     * 提示消息
     */
    private String message;
}

