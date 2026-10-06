package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.Warehouse;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.WarehouseMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仓库服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWarehouse(Warehouse warehouse, Integer userId) {
        // 检查仓库编号是否已存在
        if (warehouse.getWarehouseNumber() == null || warehouse.getWarehouseNumber().trim().isEmpty()) {
            throw new BusinessException("仓库编号不能为空");
        }

        int result = warehouseMapper.insert(warehouse);
        if (result <= 0) {
            throw new BusinessException("添加仓库失败");
        }

        // 记录操作日志（操作类型ID=5：添加仓库）
        operationLogService.logOperation(userId, 5, "成功", "warehouse", warehouse.getId(),
                "添加仓库：" + warehouse.getWarehouseNumber());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWarehouse(Integer id, Integer userId) {
        // 检查仓库是否存在
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }

        // 检查仓库下是否有货架
        Integer shelfCount = warehouseMapper.countShelfByWarehouseId(id);
        if (shelfCount > 0) {
            throw new BusinessException("该仓库下还有货架，无法删除");
        }

        int result = warehouseMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除仓库失败");
        }

        // 记录操作日志（操作类型ID=6：删除仓库）
        operationLogService.logOperation(userId, 6, "成功", "warehouse", id,
                "删除仓库：" + warehouse.getWarehouseNumber());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarehouse(Warehouse warehouse, Integer userId) {
        Warehouse oldWarehouse = warehouseMapper.selectById(warehouse.getId());
        if (oldWarehouse == null) {
            throw new BusinessException("仓库不存在");
        }

        int result = warehouseMapper.updateById(warehouse);
        if (result <= 0) {
            throw new BusinessException("修改仓库失败");
        }

        // 记录操作日志（操作类型ID=7：修改仓库）
        operationLogService.logOperation(userId, 7, "成功", "warehouse", warehouse.getId(),
                "修改仓库信息：" + warehouse.getWarehouseNumber());
    }

    @Override
    public Warehouse getWarehouseById(Integer id) {
        Warehouse warehouse = warehouseMapper.selectById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        return warehouse;
    }

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseMapper.selectList(null);
    }

    @Override
    public IPage<Warehouse> getWarehousePage(Page<Warehouse> page) {
        return warehouseMapper.selectPage(page, null);
    }
}

