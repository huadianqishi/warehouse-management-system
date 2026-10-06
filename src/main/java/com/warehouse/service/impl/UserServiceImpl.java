package com.warehouse.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.User;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.OperationLogMapper;
import com.warehouse.mapper.StockInRecordMapper;
import com.warehouse.mapper.StockOutRecordMapper;
import com.warehouse.mapper.UserMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.UserService;
import com.warehouse.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private StockInRecordMapper stockInRecordMapper;

    @Autowired
    private StockOutRecordMapper stockOutRecordMapper;

    @Override
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        User user = this.getUserByUsername(username);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        // 记录操作日志（操作类型ID=1：用户登录）
        operationLogService.logOperation(user.getId(), 1, "成功", "user", user.getId(), "用户登录");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(User user, Integer operatorId) {
        // 当 operatorId 不为 null 时，才进行权限检查和日志记录
        if (operatorId != null) {
            User operator = userMapper.selectById(operatorId);
            if (operator == null || operator.getRole() != 0) {
                throw new BusinessException("无权限添加用户");
            }
        }

        // 如果未显式指定角色，则默认设置为普通用户
        if (user.getRole() == null) {
            user.setRole(2); // 2: 普通用户
        }

        // 检查用户名是否已存在
        if (this.getUserByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        user.setPassword(BCrypt.hashpw(user.getPassword()));

        // 插入用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("添加用户失败");
        }

        // 记录操作日志（操作类型ID=2：添加用户）
        if (operatorId != null) {
            operationLogService.logOperation(operatorId, 2, "成功", "user", user.getId(), 
                    "添加用户：" + user.getUsername());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Integer id, Integer operatorId) {
        // 检查操作者ID是否存在
        if (operatorId == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        
        // 检查操作者权限
        User operator = userMapper.selectById(operatorId);
        if (operator == null) {
            throw new BusinessException("操作者不存在");
        }
        if (operator.getRole() != 0) {
            throw new BusinessException("无权限删除用户");
        }

        // 不能删除自己
        if (id != null && id.equals(operatorId)) {
            throw new BusinessException("不能删除自己");
        }

        // 检查要删除的用户ID
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 查询要删除的用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 处理用户相关的操作日志：将 user_id 更新为操作者ID（保留历史记录）
        QueryWrapper<com.warehouse.entity.OperationLog> logWrapper = new QueryWrapper<>();
        logWrapper.eq("user_id", id);
        com.warehouse.entity.OperationLog logUpdate = new com.warehouse.entity.OperationLog();
        logUpdate.setUserId(operatorId);
        operationLogMapper.update(logUpdate, logWrapper);

        // 处理用户相关的入库记录：将 operator_id 更新为操作者ID（保留历史记录）
        QueryWrapper<com.warehouse.entity.StockInRecord> inWrapper = new QueryWrapper<>();
        inWrapper.eq("operator_id", id);
        com.warehouse.entity.StockInRecord inUpdate = new com.warehouse.entity.StockInRecord();
        inUpdate.setOperatorId(operatorId);
        stockInRecordMapper.update(inUpdate, inWrapper);

        // 处理用户相关的出库记录：将 operator_id 更新为操作者ID（保留历史记录）
        QueryWrapper<com.warehouse.entity.StockOutRecord> outWrapper = new QueryWrapper<>();
        outWrapper.eq("operator_id", id);
        com.warehouse.entity.StockOutRecord outUpdate = new com.warehouse.entity.StockOutRecord();
        outUpdate.setOperatorId(operatorId);
        stockOutRecordMapper.update(outUpdate, outWrapper);

        // 删除用户
        int result = userMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除用户失败");
        }

        // 记录操作日志（操作类型ID=3：删除用户）
        // 使用try-catch确保日志记录失败不会影响删除操作
        try {
            operationLogService.logOperation(operatorId, 3, "成功", "user", id, 
                    "删除用户：" + user.getUsername());
        } catch (Exception e) {
            // 日志记录失败不影响删除操作，只记录错误日志
            log.error("记录删除用户操作日志失败：", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user, Integer operatorId) {
        // 检查操作者权限
        User operator = userMapper.selectById(operatorId);
        if (operator == null || operator.getRole() != 0) {
            throw new BusinessException("无权限修改用户信息");
        }

        // 查询原用户信息
        User oldUser = userMapper.selectById(user.getId());
        if (oldUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改了密码，需要重新加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        } else {
            user.setPassword(oldUser.getPassword());
        }

        // 更新用户
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("修改用户失败");
        }

        // 记录操作日志（操作类型ID=4：修改用户）
        operationLogService.logOperation(operatorId, 4, "成功", "user", user.getId(), 
                "修改用户信息：" + user.getUsername());
    }

    @Override
    public User getUserById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public IPage<User> getUserPage(Page<User> page) {
        IPage<User> userPage = userMapper.selectPage(page, null);
        userPage.getRecords().forEach(user -> user.setPassword(null));
        return userPage;
    }

    @Override
    public List<User> getUsersByRole(Integer role) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role", role);
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean canDeleteUser(Integer userId) {
        // 现在所有用户都可以删除（删除时会自动处理相关记录）
        // 只检查用户ID是否有效
        if (userId == null) {
            return false;
        }
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        return user != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String username, String phone) {
        // 查询用户
        User user = this.getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名不存在");
        }

        // 验证手机号
        if (!phone.equals(user.getPhone())) {
            throw new BusinessException("手机号与注册时填写的不一致");
        }

        // 重置密码为"123456"并加密
        user.setPassword(BCrypt.hashpw("123456"));

        // 更新用户
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("重置密码失败");
        }

        // 记录操作日志（操作类型ID=4：修改用户）
        operationLogService.logOperation(user.getId(), 4, "成功", "user", user.getId(), 
                "用户通过手机号重置密码");
    }
}
