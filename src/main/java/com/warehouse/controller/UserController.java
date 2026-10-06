package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.User;
import com.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 提供用户相关的API接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * POST /api/user/login
     * 参数：username, password
     * 返回：token和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> result = userService.login(user.getUsername(), user.getPassword());
        return Result.success("登录成功", result);
    }

    /**
     * 公开注册接口（默认注册为普通用户）
     * POST /api/user/register
     */
    @PostMapping("/register")
    public Result<String> publicRegister(@RequestBody User user) {
        // 注册页面统一创建普通用户账号
        user.setRole(2);
        userService.register(user, null);
        return Result.success("注册成功");
    }

    /**
     * 添加用户（仅超级管理员）
     * POST /api/user/add
     */
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody User user, HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        userService.register(user, operatorId);
        return Result.success("添加用户成功");
    }

    /**
     * 删除用户（仅超级管理员）
     * DELETE /api/user/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        if (operatorId == null) {
            return Result.error("未登录或登录已过期");
        }
        if (id == null) {
            return Result.error("用户ID不能为空");
        }
        userService.deleteUser(id, operatorId);
        return Result.success("删除用户成功");
    }

    /**
     * 修改用户信息（仅超级管理员）
     * PUT /api/user/update
     */
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user, HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        userService.updateUser(user, operatorId);
        return Result.success("修改用户成功");
    }

    /**
     * 根据ID查询用户
     * GET /api/user/get/{id}
     */
    @GetMapping("/get/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 查询所有用户
     * GET /api/user/list
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // 为每个用户添加 canDelete 字段
        List<Map<String, Object>> userList = new java.util.ArrayList<>();
        for (User user : users) {
            Map<String, Object> userMap = new java.util.HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("realName", user.getRealName());
            userMap.put("gender", user.getGender());
            userMap.put("phone", user.getPhone());
            userMap.put("idCard", user.getIdCard());
            userMap.put("role", user.getRole());
            userMap.put("driverLicense", user.getDriverLicense());
            userMap.put("licenseImage", user.getLicenseImage());
            // 检查是否可以删除
            userMap.put("canDelete", userService.canDeleteUser(user.getId()));
            userList.add(userMap);
        }
        return Result.success(userList);
    }

    /**
     * 分页查询用户
     * GET /api/user/page?current=1&size=10
     */
    @GetMapping("/page")
    public Result<?> getUserPage(@RequestParam(defaultValue = "1") Integer current,
                                  @RequestParam(defaultValue = "10") Integer size) {
        Page<User> page = new Page<>(current, size);
        return Result.success(userService.getUserPage(page));
    }

    /**
     * 根据角色查询用户
     * GET /api/user/role/{role}
     * role: 0-超级管理员, 1-仓管员, 2-普通用户
     */
    @GetMapping("/role/{role}")
    public Result<List<User>> getUsersByRole(@PathVariable Integer role) {
        List<User> users = userService.getUsersByRole(role);
        return Result.success(users);
    }

    /**
     * 重置密码（公开接口，无需认证）
     * POST /api/user/reset-password
     * 参数：username, phone
     * 功能：验证用户名和手机号后，将密码重置为"123456"
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String phone = params.get("phone");
        
        if (username == null || username.isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (phone == null || phone.isEmpty()) {
            return Result.error("手机号不能为空");
        }
        
        userService.resetPassword(username, phone);
        return Result.success("密码已重置为：123456");
    }
}

