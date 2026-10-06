package com.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.OperationLog;

import java.util.List;

/**
 * 操作记录服务接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface OperationLogService {

    /**
     * 记录操作日志（使用操作类型ID）
     */
    void logOperation(Integer userId, Integer operationTypeId, String operationStatus, 
                     String targetTable, Integer targetId, String description);

    /**
     * 查询所有操作记录
     */
    List<OperationLog> getAllLogs();

    /**
     * 分页查询操作记录
     */
    IPage<OperationLog> getLogPage(Page<OperationLog> page);

    /**
     * 根据用户ID查询操作记录
     */
    List<OperationLog> getLogsByUserId(Integer userId);

  /**
   * 根据ID删除单条操作记录
   */
  void deleteLogById(Integer id);

  /**
   * 清空所有操作记录
   */
  void clearAllLogs();
}

