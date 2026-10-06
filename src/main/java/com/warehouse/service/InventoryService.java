package com.warehouse.service;

import com.warehouse.entity.dto.SlotConflictDTO;
import com.warehouse.entity.dto.SlotStatusDTO;
import com.warehouse.entity.dto.WarehouseUtilizationDTO;

import java.util.List;

/**
 * 库存服务接口
 * 提供格口管理相关的功能
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface InventoryService {

    /**
     * 获取仓库格口利用率
     * 
     * @param warehouseId 仓库ID
     * @return 仓库利用率信息
     */
    WarehouseUtilizationDTO getWarehouseUtilization(Integer warehouseId);

    /**
     * 获取货架所有格口状态
     * 
     * @param shelfId 货架ID
     * @return 格口状态列表
     */
    List<SlotStatusDTO> getShelfSlotStatus(Integer shelfId);

    /**
     * 获取仓库的空闲格口列表
     * 
     * @param warehouseId 仓库ID
     * @return 空闲格口列表
     */
    List<SlotStatusDTO> getEmptySlots(Integer warehouseId);

    /**
     * 检查格口冲突
     * 检查指定格口是否已有不同商品占用
     * 
     * @param shelfId 货架ID
     * @param floorNumber 层数
     * @param productId 要入库的商品ID
     * @return 冲突检测结果
     */
    SlotConflictDTO checkSlotConflict(Integer shelfId, Integer floorNumber, Integer productId);

    /**
     * 获取格口的当前库存数量
     * 
     * @param shelfId 货架ID
     * @param floorNumber 层数
     * @param productId 商品ID
     * @return 当前库存数量（如果不存在则返回0）
     */
    Integer getSlotCurrentQuantity(Integer shelfId, Integer floorNumber, Integer productId);

    /**
     * 获取商品全库可用库存（总库存 - 已预占）
     *
     * @param productId 商品ID
     * @return 可用库存数量
     */
    Integer getProductAvailableStock(Integer productId);

    /**
     * 获取指定商品在所有仓库的库存位置列表
     *
     * @param productId 商品ID
     * @return 位置列表（含仓库、货架、层数、可用数量）
     */
    List<com.warehouse.entity.dto.InventoryDTO> getProductLocations(Integer productId);

    /**
     * 获取所有库存信息（分页）
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 搜索关键词（商品名称或货架编号）
     * @param warehouseId 仓库ID（可选）
     * @return 库存信息列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<com.warehouse.entity.dto.InventoryDTO> getInventoryPage(
        Integer current, Integer size, String keyword, Integer warehouseId);

    /**
     * 获取所有库存信息（列表）
     * 
     * @param keyword 搜索关键词（商品名称或货架编号）
     * @param warehouseId 仓库ID（可选）
     * @return 库存信息列表
     */
    List<com.warehouse.entity.dto.InventoryDTO> getInventoryList(String keyword, Integer warehouseId);
}

