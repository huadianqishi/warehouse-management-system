package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 入库操作记录实体类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("stock_in_record")
public class StockInRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联商品ID
     */
    private Integer productId;

    /**
     * 入库数量
     */
    private Integer quantity;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date storageTime;

    /**
     * 存放货架ID
     */
    private Integer shelfId;

    /**
     * 货架具体层数
     */
    private Integer floorNumber;

    /**
     * 商品重量（kg）
     */
    private BigDecimal weight;

    /**
     * 商品体积（m³）
     */
    private BigDecimal volume;

    /**
     * 操作人ID
     */
    private Integer operatorId;

    /**
     * 预占库存数量（物流订单锁定，待发货后释放）
     */
    private Integer lockedQuantity;

    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
}

