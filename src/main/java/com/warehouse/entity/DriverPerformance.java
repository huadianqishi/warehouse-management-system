package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 司机绩效实体类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("driver_performance")
public class DriverPerformance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 统计月份（格式：2024-05）
     */
    private String month;

    /**
     * 完成单量
     */
    private Integer orderCount;

    /**
     * 创造总收益（关联订单的实际获利总和）
     */
    private BigDecimal totalProfit;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
}
