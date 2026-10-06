package com.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.Warehouse;

import java.util.List;

/**
 * 仓库服务接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface WarehouseService {

    /**
     * 新增仓库
     */
    void addWarehouse(Warehouse warehouse, Integer userId);

    /**
     * 删除仓库（需要检查是否有货架）
     */
    void deleteWarehouse(Integer id, Integer userId);

    /**
     * 更新仓库信息
     */
    void updateWarehouse(Warehouse warehouse, Integer userId);

    /**
     * 根据ID查询仓库
     */
    Warehouse getWarehouseById(Integer id);

    /**
     * 查询所有仓库
     */
    List<Warehouse> getAllWarehouses();

    /**
     * 分页查询仓库
     */
    IPage<Warehouse> getWarehousePage(Page<Warehouse> page);
}

