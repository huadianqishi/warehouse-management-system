package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.Warehouse;
import com.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 仓库控制器
 * 提供仓库管理相关的API接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * 添加仓库
     * POST /api/warehouse/add
     * 权限：仓管员、超级管理员
     */
    @PostMapping("/add")
    public Result<String> addWarehouse(@RequestBody Warehouse warehouse, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        warehouseService.addWarehouse(warehouse, userId);
        return Result.success("添加仓库成功");
    }

    /**
     * 删除仓库
     * DELETE /api/warehouse/delete/{id}
     * 权限：仓管员、超级管理员
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteWarehouse(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        warehouseService.deleteWarehouse(id, userId);
        return Result.success("删除仓库成功");
    }

    /**
     * 修改仓库信息
     * PUT /api/warehouse/update
     * 权限：仓管员、超级管理员
     */
    @PutMapping("/update")
    public Result<String> updateWarehouse(@RequestBody Warehouse warehouse, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        warehouseService.updateWarehouse(warehouse, userId);
        return Result.success("修改仓库成功");
    }

    /**
     * 根据ID查询仓库
     * GET /api/warehouse/get/{id}
     */
    @GetMapping("/get/{id}")
    public Result<Warehouse> getWarehouseById(@PathVariable Integer id) {
        Warehouse warehouse = warehouseService.getWarehouseById(id);
        return Result.success(warehouse);
    }

    /**
     * 查询所有仓库
     * GET /api/warehouse/list
     */
    @GetMapping("/list")
    public Result<List<Warehouse>> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        return Result.success(warehouses);
    }

    /**
     * 分页查询仓库
     * GET /api/warehouse/page?current=1&size=10
     */
    @GetMapping("/page")
    public Result<?> getWarehousePage(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size) {
        Page<Warehouse> page = new Page<>(current, size);
        return Result.success(warehouseService.getWarehousePage(page));
    }
}

