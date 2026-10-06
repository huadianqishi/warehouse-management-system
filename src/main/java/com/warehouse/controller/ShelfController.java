package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.Shelf;
import com.warehouse.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 货架控制器
 * 提供货架管理相关的API接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/shelf")
public class ShelfController {

    @Autowired
    private ShelfService shelfService;

    /**
     * 添加货架
     * POST /api/shelf/add
     * 权限：仓管员、超级管理员
     */
    @PostMapping("/add")
    public Result<String> addShelf(@RequestBody Shelf shelf, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        shelfService.addShelf(shelf, userId);
        return Result.success("添加货架成功");
    }

    /**
     * 删除货架
     * DELETE /api/shelf/delete/{id}
     * 权限：仓管员、超级管理员
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteShelf(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        shelfService.deleteShelf(id, userId);
        return Result.success("删除货架成功");
    }

    /**
     * 修改货架信息
     * PUT /api/shelf/update
     * 权限：仓管员、超级管理员
     */
    @PutMapping("/update")
    public Result<String> updateShelf(@RequestBody Shelf shelf, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        shelfService.updateShelf(shelf, userId);
        return Result.success("修改货架成功");
    }

    /**
     * 根据ID查询货架
     * GET /api/shelf/get/{id}
     */
    @GetMapping("/get/{id}")
    public Result<Shelf> getShelfById(@PathVariable Integer id) {
        Shelf shelf = shelfService.getShelfById(id);
        return Result.success(shelf);
    }

    /**
     * 查询所有货架
     * GET /api/shelf/list
     */
    @GetMapping("/list")
    public Result<List<Shelf>> getAllShelves() {
        List<Shelf> shelves = shelfService.getAllShelves();
        return Result.success(shelves);
    }

    /**
     * 根据仓库ID查询货架
     * GET /api/shelf/warehouse/{warehouseId}
     */
    @GetMapping("/warehouse/{warehouseId}")
    public Result<List<Shelf>> getShelvesByWarehouseId(@PathVariable Integer warehouseId) {
        List<Shelf> shelves = shelfService.getShelvesByWarehouseId(warehouseId);
        return Result.success(shelves);
    }

    /**
     * 分页查询货架
     * GET /api/shelf/page?current=1&size=10&warehouseId=1
     */
    @GetMapping("/page")
    public Result<?> getShelfPage(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) Integer warehouseId) {
        Page<Shelf> page = new Page<>(current, size);
        return Result.success(shelfService.getShelfPage(page, warehouseId));
    }
}

