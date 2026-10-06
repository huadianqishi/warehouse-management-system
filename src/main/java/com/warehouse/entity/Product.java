package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品实体类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品类型
     */
    private String type;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 安全库存，用于低库存预警
     */
    private Integer safetyStock;
}

