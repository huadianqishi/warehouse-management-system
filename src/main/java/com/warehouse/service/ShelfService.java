package com.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.Shelf;

import java.util.List;

/**
 * 货架服务接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface ShelfService {

    /**
     * 新增货架
     */
    void addShelf(Shelf shelf, Integer userId);

    /**
     * 删除货架（需要检查是否有商品）
     */
    void deleteShelf(Integer id, Integer userId);

    /**
     * 更新货架信息
     */
    void updateShelf(Shelf shelf, Integer userId);

    /**
     * 根据ID查询货架
     */
    Shelf getShelfById(Integer id);

    /**
     * 查询所有货架
     */
    List<Shelf> getAllShelves();

    /**
     * 根据仓库ID查询货架
     */
    List<Shelf> getShelvesByWarehouseId(Integer warehouseId);

    /**
     * 分页查询货架
     */
    IPage<Shelf> getShelfPage(Page<Shelf> page, Integer warehouseId);
}

