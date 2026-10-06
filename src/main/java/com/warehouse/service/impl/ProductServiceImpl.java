package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.Product;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.ProductMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(Product product, Integer userId) {
        // 验证必填字段
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BusinessException("商品名称不能为空");
        }
        if (product.getType() == null || product.getType().trim().isEmpty()) {
            throw new BusinessException("商品类型不能为空");
        }
        if (product.getUnitPrice() == null || product.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException("商品单价不能为空且必须大于等于0");
        }
        if (product.getSafetyStock() == null) {
            product.setSafetyStock(0);
        }

        int result = productMapper.insert(product);
        if (result <= 0) {
            throw new BusinessException("添加商品失败");
        }

        // 记录操作日志（操作类型ID=11：添加商品）
        operationLogService.logOperation(userId, 11, "成功", "product", product.getId(),
                "添加商品：" + product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Integer id, Integer userId) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查商品是否有关联的入库/出库记录（这里需要检查stock_in_record和stock_out_record表）
        // 如果有库存记录，不允许删除商品基础信息
        // 注意：实际实现中需要注入StockInRecordMapper和StockOutRecordMapper来检查

        int result = productMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除商品失败");
        }

        // 记录操作日志（操作类型ID=12：删除商品）
        operationLogService.logOperation(userId, 12, "成功", "product", id,
                "删除商品：" + product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Product product, Integer userId) {
        Product oldProduct = productMapper.selectById(product.getId());
        if (oldProduct == null) {
            throw new BusinessException("商品不存在");
        }

        // 验证必填字段
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BusinessException("商品名称不能为空");
        }
        if (product.getType() == null || product.getType().trim().isEmpty()) {
            throw new BusinessException("商品类型不能为空");
        }
        if (product.getUnitPrice() == null || product.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException("商品单价不能为空且必须大于等于0");
        }
        if (product.getSafetyStock() == null) {
            product.setSafetyStock(0);
        }

        int result = productMapper.updateById(product);
        if (result <= 0) {
            throw new BusinessException("修改商品失败");
        }

        // 记录操作日志（操作类型ID=13：修改商品）
        operationLogService.logOperation(userId, 13, "成功", "product", product.getId(),
                "修改商品信息：" + product.getName());
    }

    @Override
    public Product getProductById(Integer id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectList(null);
    }

    @Override
    public IPage<Product> getProductPage(Page<Product> page) {
        return productMapper.selectPage(page, null);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword).or().like("type", keyword);
        return productMapper.selectList(wrapper);
    }
}

