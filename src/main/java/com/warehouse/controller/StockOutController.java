package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.StockOutRecord;
import com.warehouse.service.StockOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 出库操作控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Slf4j
@RestController
@RequestMapping("/stock-out")
public class StockOutController {

    @Autowired
    private StockOutService stockOutService;

    /**
     * 添加出库记录
     * POST /api/stock-out/add
     * 权限：仓管员、超级管理员
     */
    @PostMapping("/add")
    public Result<String> addStockOut(@RequestBody StockOutRecord stockOutRecord, HttpServletRequest request) {
        log.info("收到出库请求：productId={}, quantity={}, shelfId={}, floorNumber={}, destination={}",
            stockOutRecord.getProductId(),
            stockOutRecord.getQuantity(),
            stockOutRecord.getShelfId(),
            stockOutRecord.getFloorNumber(),
            stockOutRecord.getDestination());

        Integer operatorId = (Integer) request.getAttribute("userId");
        log.info("操作人ID：{}", operatorId);

        try {
            stockOutService.addStockOut(stockOutRecord, operatorId);
            log.info("出库成功：记录ID={}", stockOutRecord.getId());
            return Result.success("出库成功");
        } catch (Exception e) {
            log.error("出库失败：", e);
            throw e;
        }
    }
}
