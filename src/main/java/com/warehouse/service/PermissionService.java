package com.warehouse.service;

import com.warehouse.entity.Permission;

/**
 * 权限服务接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface PermissionService {

    /**
     * 根据权限名称获取权限
     */
    Permission getPermissionByName(String name);

    /**
     * 检查用户是否有权限
     * @param role 用户角色（0=超级管理员，1=仓管员，2=普通用户）
     * @param permissionName 权限名称
     * @return true表示有权限，false表示无权限
     */
    boolean hasPermission(Integer role, String permissionName);
}

