package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物流订单实体类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("logistics_order")
public class LogisticsOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 运单号（唯一索引）
     */
    private String orderNo;

    /**
     * 状态：0-待指派, 1-已指派/待装货, 2-运输中, 3-已完成, 4-已取消
     */
    private Integer status;

    /**
     * 发货人ID（关联 address_book）
     */
    private Integer senderId;

    /**
     * 收货人ID（关联 address_book）
     */
    private Integer receiverId;

    /**
     * 收货信息快照（JSON格式，防止地址变动影响历史订单）
     */
    private String receiverSnapshot;

    /**
     * 承运司机ID
     */
    private Integer driverId;

    /**
     * 承运车辆ID
     */
    private Integer vehicleId;

    /**
     * 订单总重量（冗余字段，用于快速匹配车辆）
     */
    private BigDecimal totalWeight;

    /**
     * 订单总体积（冗余字段，用于快速匹配车辆）
     */
    private BigDecimal totalVolume;

    /**
     * 预估送达时间（倒计时结束点）
     */
    private LocalDateTime estimatedTime;

    /**
     * 实际出发时间（倒计时开始点）
     */
    private LocalDateTime startTime;

    /**
     * 实际送达时间
     */
    private LocalDateTime actualArrivalTime;

    /**
     * 预估利润（收入-成本）
     */
    private BigDecimal profitEstimate;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;
}
