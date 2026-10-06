package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 货架实体类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("shelf")
public class Shelf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 货架业务编号（用于展示和生成二维码）
     */
    private String shelfNumber;

    /**
     * 层数
     */
    private Integer floorCount;

    /**
     * 区域分类（如：文具区、电子产品区）
     */
    private String areaCategory;

    /**
     * 所属仓库ID
     */
    private Integer warehouseId;
}

