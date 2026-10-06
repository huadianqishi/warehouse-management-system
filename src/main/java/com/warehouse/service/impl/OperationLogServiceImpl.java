package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.OperationLog;
import com.warehouse.mapper.OperationLogMapper;
import com.warehouse.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 操作记录服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void logOperation(Integer userId, Integer operationTypeId, String operationStatus,
                            String targetTable, Integer targetId, String description) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setOperationTypeId(operationTypeId);
        log.setOperationTime(new Date());
        log.setOperationStatus(operationStatus);
        log.setTargetTable(targetTable);
        log.setTargetId(targetId);
        log.setDescription(description);
        
        operationLogMapper.insert(log);
    }

    @Override
    public List<OperationLog> getAllLogs() {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("operation_time");
        return operationLogMapper.selectList(wrapper);
    }

    @Override
    public IPage<OperationLog> getLogPage(Page<OperationLog> page) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("operation_time");
        return operationLogMapper.selectPage(page, wrapper);
    }

    @Override
    public List<OperationLog> getLogsByUserId(Integer userId) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("operation_time");
        return operationLogMapper.selectList(wrapper);
    }

    @Override
    public void deleteLogById(Integer id) {
        if (id != null) {
            operationLogMapper.deleteById(id);
        }
    }

    @Override
    public void clearAllLogs() {
        // 直接清空表中的所有记录
        operationLogMapper.delete(null);
    }
}

