package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别（男1，女0）
     */
    private Integer gender;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 角色
     * 0: 超级管理员 - 拥有全部权限
     * 1: 仓管员 - 可新增商品、仓库、货架，但不能修改或删除
     * 2: 普通用户 - 只能查看基础数据，无任何编辑权限
     * 3: 司机 - 承运物流订单
     */
    private Integer role;

    /**
     * 驾驶证号码（司机必填）
     */
    private String driverLicense;

    /**
     * 证件照片路径
     */
    private String licenseImage;
}
