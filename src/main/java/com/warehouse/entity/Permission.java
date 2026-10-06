package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限实体类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 权限名称（如"商品查询"）
     */
    private String name;

    /**
     * 权限等级
     * 0 = 仅超级管理员
     * 1 = 超级管理员 + 仓管员
     * 2 = 所有人（含普通用户）
     */
    private Integer level;
}

