package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.Shelf;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.ShelfMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 货架服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class ShelfServiceImpl implements ShelfService {

    @Autowired
    private ShelfMapper shelfMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addShelf(Shelf shelf, Integer userId) {
        // 验证必填字段
        if (shelf.getShelfNumber() == null || shelf.getShelfNumber().trim().isEmpty()) {
            throw new BusinessException("货架编号不能为空");
        }
        if (shelf.getWarehouseId() == null) {
            throw new BusinessException("所属仓库不能为空");
        }
        if (shelf.getFloorCount() == null || shelf.getFloorCount() <= 0) {
            throw new BusinessException("层数必须大于0");
        }
        if (shelf.getAreaCategory() == null || shelf.getAreaCategory().trim().isEmpty()) {
            throw new BusinessException("区域分类不能为空");
        }

        // 检查同一仓库内货架编号是否已存在（由数据库唯一约束保证，这里可以提前检查）
        
        int result = shelfMapper.insert(shelf);
        if (result <= 0) {
            throw new BusinessException("添加货架失败，可能货架编号已存在");
        }

        // 记录操作日志（操作类型ID=8：添加货架）
        operationLogService.logOperation(userId, 8, "成功", "shelf", shelf.getId(),
                "添加货架：" + shelf.getShelfNumber());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShelf(Integer id, Integer userId) {
        // 检查货架是否存在
        Shelf shelf = shelfMapper.selectById(id);
        if (shelf == null) {
            throw new BusinessException("货架不存在");
        }

        // 检查货架上是否有库存记录
        // 注意：由于商品表不再关联货架，需要检查stock_in_record表
        // 这里暂时跳过检查，实际实现中需要注入StockInRecordMapper来检查
        // 如果货架上有库存记录，不允许删除

        int result = shelfMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除货架失败");
        }

        // 记录操作日志（操作类型ID=9：删除货架）
        operationLogService.logOperation(userId, 9, "成功", "shelf", id,
                "删除货架：" + shelf.getShelfNumber());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShelf(Shelf shelf, Integer userId) {
        Shelf oldShelf = shelfMapper.selectById(shelf.getId());
        if (oldShelf == null) {
            throw new BusinessException("货架不存在");
        }

        int result = shelfMapper.updateById(shelf);
        if (result <= 0) {
            throw new BusinessException("修改货架失败");
        }

        // 记录操作日志（操作类型ID=10：修改货架）
        operationLogService.logOperation(userId, 10, "成功", "shelf", shelf.getId(),
                "修改货架信息：" + shelf.getShelfNumber());
    }

    @Override
    public Shelf getShelfById(Integer id) {
        Shelf shelf = shelfMapper.selectById(id);
        if (shelf == null) {
            throw new BusinessException("货架不存在");
        }
        return shelf;
    }

    @Override
    public List<Shelf> getAllShelves() {
        return shelfMapper.selectList(null);
    }

    @Override
    public List<Shelf> getShelvesByWarehouseId(Integer warehouseId) {
        QueryWrapper<Shelf> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", warehouseId);
        return shelfMapper.selectList(wrapper);
    }

    @Override
    public IPage<Shelf> getShelfPage(Page<Shelf> page, Integer warehouseId) {
        QueryWrapper<Shelf> wrapper = new QueryWrapper<>();
        if (warehouseId != null) {
            wrapper.eq("warehouse_id", warehouseId);
        }
        return shelfMapper.selectPage(page, wrapper);
    }
}

