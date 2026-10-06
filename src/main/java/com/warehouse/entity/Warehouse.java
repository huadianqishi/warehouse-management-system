package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 仓库实体类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("warehouse")
public class Warehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 仓库号码（唯一）
     */
    private String warehouseNumber;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 仓库管理者姓名
     */
    private String managerName;

    /**
     * 管理者联系方式
     */
    private String managerPhone;
}

