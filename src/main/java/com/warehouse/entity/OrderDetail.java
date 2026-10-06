package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品关联实体类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联物流订单ID
     */
    private Integer orderId;

    /**
     * 关联商品ID
     */
    private Integer productId;

    /**
     * 发货数量
     */
    private Integer quantity;

    /**
     * 该批次总重量（kg）
     */
    private BigDecimal weight;

    /**
     * 该批次总体积（m³）
     */
    private BigDecimal volume;

    /**
     * 对应入库批次号
     */
    private String batchNo;

    /**
     * 预占库存对应的 stock_in_record 主键ID
     */
    @TableField(exist = false)
    private Integer stockRecordId;

    /**
     * 预占库位所在货架ID
     */
    @TableField(exist = false)
    private Integer shelfId;

    /**
     * 预占库位所在层数
     */
    @TableField(exist = false)
    private Integer floorNumber;
}
