package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.StockInRecord;
import com.warehouse.service.StockInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 入库操作控制器
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Slf4j
@RestController
@RequestMapping("/stock-in")
public class StockInController {

    @Autowired
    private StockInService stockInService;

    /**
     * 添加入库记录
     * POST /api/stock-in/add
     * 权限：仓管员、超级管理员
     */
    @PostMapping("/add")
    public Result<String> addStockIn(@RequestBody StockInRecord stockInRecord, HttpServletRequest request) {
        log.info("收到入库请求：productId={}, quantity={}, shelfId={}, floorNumber={}, weight={}, volume={}", 
            stockInRecord.getProductId(), 
            stockInRecord.getQuantity(), 
            stockInRecord.getShelfId(), 
            stockInRecord.getFloorNumber(),
            stockInRecord.getWeight(),
            stockInRecord.getVolume());
        
        Integer operatorId = (Integer) request.getAttribute("userId");
        log.info("操作人ID：{}", operatorId);
        
        try {
            stockInService.addStockIn(stockInRecord, operatorId);
            log.info("入库成功：记录ID={}", stockInRecord.getId());
            return Result.success("入库成功");
        } catch (Exception e) {
            log.error("入库失败：", e);
            throw e; // 重新抛出异常，让全局异常处理器处理
        }
    }
}

