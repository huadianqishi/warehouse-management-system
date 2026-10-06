package com.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface ProductService {

    /**
     * 新增商品
     */
    void addProduct(Product product, Integer userId);

    /**
     * 删除商品
     */
    void deleteProduct(Integer id, Integer userId);

    /**
     * 更新商品信息
     */
    void updateProduct(Product product, Integer userId);

    /**
     * 根据ID查询商品
     */
    Product getProductById(Integer id);

    /**
     * 查询所有商品
     */
    List<Product> getAllProducts();

    /**
     * 分页查询商品
     */
    IPage<Product> getProductPage(Page<Product> page);

    /**
     * 模糊查询商品（根据商品名称或运单ID）
     */
    List<Product> searchProducts(String keyword);
}

