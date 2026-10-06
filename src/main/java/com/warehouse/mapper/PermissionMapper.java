package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 权限Mapper接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限名称查询权限
     */
    @Select("SELECT * FROM permission WHERE name = #{name}")
    Permission getPermissionByName(String name);
}
