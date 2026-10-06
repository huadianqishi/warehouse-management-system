package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.OperationLog;
import com.warehouse.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作记录控制器
 * 提供操作记录查询相关的API接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 查询所有操作记录
     * GET /api/log/list
     */
    @GetMapping("/list")
    public Result<List<OperationLog>> getAllLogs() {
        List<OperationLog> logs = operationLogService.getAllLogs();
        return Result.success(logs);
    }

    /**
     * 分页查询操作记录
     * GET /api/log/page?current=1&size=10
     */
    @GetMapping("/page")
    public Result<?> getLogPage(@RequestParam(defaultValue = "1") Integer current,
                                 @RequestParam(defaultValue = "10") Integer size) {
        Page<OperationLog> page = new Page<>(current, size);
        return Result.success(operationLogService.getLogPage(page));
    }

    /**
     * 根据用户ID查询操作记录
     * GET /api/log/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<List<OperationLog>> getLogsByUserId(@PathVariable Integer userId) {
        List<OperationLog> logs = operationLogService.getLogsByUserId(userId);
        return Result.success(logs);
    }

  /**
   * 根据ID删除单条操作记录
   * DELETE /api/log/delete/{id}
   */
  @DeleteMapping("/delete/{id}")
  public Result<?> deleteLogById(@PathVariable Integer id) {
      operationLogService.deleteLogById(id);
      return Result.success(null);
  }

  /**
   * 清空所有操作记录
   * DELETE /api/log/clear
   */
  @DeleteMapping("/clear")
  public Result<?> clearAllLogs() {
      operationLogService.clearAllLogs();
      return Result.success(null);
  }
}

