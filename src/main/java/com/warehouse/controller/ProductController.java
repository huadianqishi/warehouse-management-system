package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.Product;
import com.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品控制器
 * 提供商品管理相关的API接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 添加商品
     * POST /api/product/add
     * 权限：仓管员、超级管理员
     */
    @PostMapping("/add")
    public Result<String> addProduct(@RequestBody Product product, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        productService.addProduct(product, userId);
        return Result.success("添加商品成功");
    }

    /**
     * 删除商品
     * DELETE /api/product/delete/{id}
     * 权限：仓管员、超级管理员
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteProduct(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        productService.deleteProduct(id, userId);
        return Result.success("删除商品成功");
    }

    /**
     * 修改商品信息
     * PUT /api/product/update
     * 权限：仓管员、超级管理员
     */
    @PutMapping("/update")
    public Result<String> updateProduct(@RequestBody Product product, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        productService.updateProduct(product, userId);
        return Result.success("修改商品成功");
    }

    /**
     * 根据ID查询商品
     * GET /api/product/get/{id}
     */
    @GetMapping("/get/{id}")
    public Result<Product> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    /**
     * 查询所有商品
     * GET /api/product/list
     */
    @GetMapping("/list")
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }

    /**
     * 分页查询商品
     * GET /api/product/page?current=1&size=10
     */
    @GetMapping("/page")
    public Result<?> getProductPage(@RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> page = new Page<>(current, size);
        return Result.success(productService.getProductPage(page));
    }

    /**
     * 搜索商品（根据商品名称或类型模糊查询）
     * GET /api/product/search?keyword=xxx
     */
    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return Result.success(products);
    }
}

