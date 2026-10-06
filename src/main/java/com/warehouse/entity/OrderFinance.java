package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单费用主表实体类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("order_finance")
public class OrderFinance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联物流订单ID（唯一）
     */
    private Integer orderId;

    /**
     * 总营收（运费收入）
     */
    private BigDecimal totalRevenue;

    /**
     * 总成本（油费+路费+其他）
     */
    private BigDecimal totalCost;

    /**
     * 实际获利（总营收 - 总成本）
     */
    private BigDecimal actualProfit;

    /**
     * 结算状态：0-未结, 1-待审, 2-已结
     */
    private Integer settlementStatus;

    /**
     * 下单时订单状态快照：0-待指派, 1-已指派, 2-运输中, 3-已完成, 4-已取消
     * 仅已完成(status=3)的订单才可计入财务统计
     */
    private Integer orderStatus;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;
}
