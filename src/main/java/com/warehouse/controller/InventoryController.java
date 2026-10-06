package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.dto.SlotConflictDTO;
import com.warehouse.entity.dto.SlotStatusDTO;
import com.warehouse.entity.dto.WarehouseUtilizationDTO;
import com.warehouse.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理控制器
 * 提供格口管理相关的API接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * 获取仓库格口利用率
     * GET /api/inventory/warehouse/{warehouseId}/utilization
     */
    @GetMapping("/warehouse/{warehouseId}/utilization")
    public Result<WarehouseUtilizationDTO> getWarehouseUtilization(@PathVariable Integer warehouseId) {
        WarehouseUtilizationDTO utilization = inventoryService.getWarehouseUtilization(warehouseId);
        return Result.success(utilization);
    }

    /**
     * 获取货架所有格口状态
     * GET /api/inventory/shelf/{shelfId}/slots
     */
    @GetMapping("/shelf/{shelfId}/slots")
    public Result<List<SlotStatusDTO>> getShelfSlotStatus(@PathVariable Integer shelfId) {
        List<SlotStatusDTO> slots = inventoryService.getShelfSlotStatus(shelfId);
        return Result.success(slots);
    }

    /**
     * 获取仓库的空闲格口列表
     * GET /api/inventory/warehouse/{warehouseId}/empty-slots
     */
    @GetMapping("/warehouse/{warehouseId}/empty-slots")
    public Result<List<SlotStatusDTO>> getEmptySlots(@PathVariable Integer warehouseId) {
        List<SlotStatusDTO> emptySlots = inventoryService.getEmptySlots(warehouseId);
        return Result.success(emptySlots);
    }

    /**
     * 检查格口冲突
     * POST /api/inventory/check-slot-conflict
     * 请求体：{ "shelfId": 1, "floorNumber": 1, "productId": 2 }
     */
    @PostMapping("/check-slot-conflict")
    public Result<SlotConflictDTO> checkSlotConflict(@RequestBody SlotConflictRequest request) {
        SlotConflictDTO conflict = inventoryService.checkSlotConflict(
            request.getShelfId(), 
            request.getFloorNumber(), 
            request.getProductId()
        );
        return Result.success(conflict);
    }

    /**
     * 获取格口的当前库存数量
     * GET /api/inventory/slot/quantity?shelfId=1&floorNumber=1&productId=2
     */
    @GetMapping("/slot/quantity")
    public Result<Integer> getSlotCurrentQuantity(
            @RequestParam Integer shelfId,
            @RequestParam Integer floorNumber,
            @RequestParam Integer productId) {
        Integer quantity = inventoryService.getSlotCurrentQuantity(shelfId, floorNumber, productId);
        return Result.success(quantity);
    }

    /**
     * 获取商品全库可用库存（总库存 - 已预占）
     * GET /api/inventory/product/{productId}/available
     */
    @GetMapping("/product/{productId}/available")
    public Result<Integer> getProductAvailableStock(@PathVariable Integer productId) {
        Integer available = inventoryService.getProductAvailableStock(productId);
        return Result.success(available);
    }

    /**
     * 获取指定商品的所有库存位置（仓库+货架+层数+数量）
     * GET /api/inventory/product/{productId}/locations
     */
    @GetMapping("/product/{productId}/locations")
    public Result<?> getProductLocations(@PathVariable Integer productId) {
        return Result.success(inventoryService.getProductLocations(productId));
    }

    /**
     * 获取库存列表（分页）
     * GET /api/inventory/list?current=1&size=10&keyword=xxx&warehouseId=1
     */
    @GetMapping("/list")
    public Result<?> getInventoryList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer warehouseId) {
        return Result.success(inventoryService.getInventoryPage(current, size, keyword, warehouseId));
    }

    /**
     * 调试接口：检查入库记录
     * GET /api/inventory/debug/stock-in-records
     */
    @GetMapping("/debug/stock-in-records")
    public Result<?> debugStockInRecords() {
        // 这个接口用于调试，检查入库记录是否存在
        // 实际实现可以注入 StockInRecordMapper 来查询
        return Result.success("调试接口：请查看后端日志中的入库记录查询结果");
    }

    /**
     * 格口冲突检测请求类
     */
    public static class SlotConflictRequest {
        private Integer shelfId;
        private Integer floorNumber;
        private Integer productId;

        public Integer getShelfId() {
            return shelfId;
        }

        public void setShelfId(Integer shelfId) {
            this.shelfId = shelfId;
        }

        public Integer getFloorNumber() {
            return floorNumber;
        }

        public void setFloorNumber(Integer floorNumber) {
            this.floorNumber = floorNumber;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }
    }
}

