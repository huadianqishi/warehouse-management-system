package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 车辆实体类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("vehicle")
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 车牌号（唯一）
     */
    private String plateNumber;

    /**
     * 车型：0-微面, 1-小货, 2-中货, 3-大货
     */
    private Integer category;

    /**
     * 最大载重（吨）
     */
    private BigDecimal maxWeight;

    /**
     * 最大容积（立方）
     */
    private BigDecimal maxVolume;

    /**
     * 状态：0-空闲, 1-运输中, 2-维修
     */
    private Integer status;

    /**
     * 当前绑定司机ID（关联 user.id）
     */
    private Integer currentDriverId;

    /**
     * 当前绑定司机姓名（关联查询，不对应数据库字段）
     */
    @TableField(exist = false)
    private String driverName;

    /**
     * 车辆备注
     */
    private String remark;
}
