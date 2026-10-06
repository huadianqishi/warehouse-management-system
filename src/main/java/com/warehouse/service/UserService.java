package com.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface UserService {

    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password);

    /**
     * 用户注册（仅超级管理员可用）
     */
    void register(User user, Integer operatorId);

    /**
     * 删除用户（仅超级管理员可用，保留历史记录）
     */
    void deleteUser(Integer id, Integer operatorId);

    /**
     * 更新用户信息
     */
    void updateUser(User user, Integer operatorId);

    /**
     * 根据ID查询用户
     */
    User getUserById(Integer id);

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 检查用户是否可以删除
     * @param userId 用户ID
     * @return true表示可以删除，false表示不能删除
     */
    boolean canDeleteUser(Integer userId);

    /**
     * 分页查询用户
     */
    IPage<User> getUserPage(Page<User> page);

    /**
     * 根据角色查询用户
     */
    List<User> getUsersByRole(Integer role);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 重置密码（通过用户名和手机号验证）
     */
    void resetPassword(String username, String phone);
}

