package com.warehouse.service.impl;

import com.warehouse.entity.Permission;
import com.warehouse.mapper.PermissionMapper;
import com.warehouse.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Permission getPermissionByName(String name) {
        return permissionMapper.getPermissionByName(name);
    }

    @Override
    public boolean hasPermission(Integer role, String permissionName) {
        Permission permission = getPermissionByName(permissionName);
        if (permission == null) {
            // 如果权限不存在，默认拒绝访问
            return false;
        }

        Integer level = permission.getLevel();
        
        // level 0: 仅超级管理员
        if (level == 0) {
            return role == 0;
        }
        
        // level 1: 超级管理员 + 仓管员
        if (level == 1) {
            return role == 0 || role == 1;
        }
        
        // level 2: 所有人（含普通用户）
        if (level == 2) {
            return role == 0 || role == 1 || role == 2;
        }

        return false;
    }
}

