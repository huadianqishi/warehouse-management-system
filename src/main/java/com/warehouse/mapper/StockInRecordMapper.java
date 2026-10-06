package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.StockInRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 入库记录Mapper接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface StockInRecordMapper extends BaseMapper<StockInRecord> {

    /**
     * 获取格口的当前库存数量（入库-出库）
     * 
     * @param shelfId 货架ID
     * @param floorNumber 层数
     * @param productId 商品ID
     * @return 当前库存数量
     */
    @Select("SELECT COALESCE(SUM(CASE WHEN type = 'IN' THEN quantity ELSE -quantity END), 0) AS quantity " +
            "FROM (" +
            "  SELECT quantity, 'IN' AS type FROM stock_in_record " +
            "  WHERE shelf_id = #{shelfId} AND floor_number = #{floorNumber} AND product_id = #{productId} " +
            "  UNION ALL " +
            "  SELECT quantity, 'OUT' AS type FROM stock_out_record " +
            "  WHERE shelf_id = #{shelfId} AND floor_number = #{floorNumber} AND product_id = #{productId} " +
            ") AS inventory_movement")
    Integer getSlotCurrentQuantity(@Param("shelfId") Integer shelfId, 
                                    @Param("floorNumber") Integer floorNumber, 
                                    @Param("productId") Integer productId);

    /**
     * 获取格口的所有商品当前库存（不区分商品）
     * 
     * @param shelfId 货架ID
     * @param floorNumber 层数
     * @return 商品ID和库存数量的映射
     */
    @Select("SELECT product_id AS product_id, SUM(CASE WHEN type = 'IN' THEN quantity ELSE -quantity END) AS quantity " +
            "FROM (" +
            "  SELECT product_id, quantity, 'IN' AS type FROM stock_in_record " +
            "  WHERE shelf_id = #{shelfId} AND floor_number = #{floorNumber} " +
            "  UNION ALL " +
            "  SELECT product_id, quantity, 'OUT' AS type FROM stock_out_record " +
            "  WHERE shelf_id = #{shelfId} AND floor_number = #{floorNumber} " +
            ") AS inventory_movement " +
            "GROUP BY product_id " +
            "HAVING SUM(CASE WHEN type = 'IN' THEN quantity ELSE -quantity END) > 0")
    List<Map<String, Object>> getSlotAllProducts(@Param("shelfId") Integer shelfId, 
                                                  @Param("floorNumber") Integer floorNumber);

    /**
     * 获取格口指定商品的总重量和总体积
     * 
     * @param shelfId 货架ID
     * @param floorNumber 层数
     * @param productId 商品ID
     * @return 包含总重量和总体积的Map
     */
    @Select("SELECT " +
            "  COALESCE(SUM(weight), 0) AS total_weight, " +
            "  COALESCE(SUM(volume), 0) AS total_volume " +
            "FROM stock_in_record " +
            "WHERE shelf_id = #{shelfId} AND floor_number = #{floorNumber} AND product_id = #{productId}")
    Map<String, Object> getSlotWeightAndVolume(@Param("shelfId") Integer shelfId,
                                               @Param("floorNumber") Integer floorNumber,
                                               @Param("productId") Integer productId);

    /**
     * 查询某商品在某货架格口上可用的库存（总库存 - 已预占）
     * 返回：格口ID（stock_in_record.id）、shelfId、floorNumber、availableQty（可用数量）
     */
    @Select("SELECT id, shelf_id, floor_number, " +
            "  (quantity - COALESCE(locked_quantity, 0)) AS available_quantity " +
            "FROM stock_in_record " +
            "WHERE product_id = #{productId} " +
            "  AND shelf_id = #{shelfId} " +
            "  AND floor_number = #{floorNumber} " +
            "  AND (quantity - COALESCE(locked_quantity, 0)) >= #{requiredQty} " +
            "LIMIT 1")
    Map<String, Object> findAvailableStockSlot(@Param("productId") Integer productId,
                                              @Param("shelfId") Integer shelfId,
                                              @Param("floorNumber") Integer floorNumber,
                                              @Param("requiredQty") Integer requiredQty);

    /**
     * 查询某商品的第一个可用库存格口（总库存 - 已预占 >= 需求数量）
     */
    @Select("SELECT id, shelf_id, floor_number, " +
            "  (quantity - COALESCE(locked_quantity, 0)) AS available_quantity " +
            "FROM stock_in_record " +
            "WHERE product_id = #{productId} " +
            "  AND (quantity - COALESCE(locked_quantity, 0)) >= #{requiredQty} " +
            "ORDER BY storage_time ASC LIMIT 1")
    Map<String, Object> findFirstAvailableStock(@Param("productId") Integer productId,
                                                @Param("requiredQty") Integer requiredQty);

    /**
     * 查询商品全库可用库存（入库总库存 - 已预占 - 已出库）
     */
    @Select("SELECT COALESCE(SUM(net_available), 0) AS available_quantity FROM (" +
            "  SELECT (quantity - COALESCE(locked_quantity, 0)) AS net_available " +
            "  FROM stock_in_record WHERE product_id = #{productId} " +
            "  UNION ALL " +
            "  SELECT -quantity AS net_available " +
            "  FROM stock_out_record WHERE product_id = #{productId}" +
            ") AS t")
    Integer getTotalAvailableStock(@Param("productId") Integer productId);

    /**
     * 预占库存：给指定 stock_in_record 的 locked_quantity 增加指定数量
     */
    @Update("UPDATE stock_in_record " +
            "SET locked_quantity = COALESCE(locked_quantity, 0) + #{lockQty} " +
            "WHERE id = #{recordId}")
    int lockStock(@Param("recordId") Integer recordId, @Param("lockQty") Integer lockQty);

    /**
     * 释放预占库存：从 locked_quantity 中减去指定数量
     */
    @Update("UPDATE stock_in_record " +
            "SET locked_quantity = GREATEST(0, COALESCE(locked_quantity, 0) - #{unlockQty}) " +
            "WHERE id = #{recordId}")
    int unlockStock(@Param("recordId") Integer recordId, @Param("unlockQty") Integer unlockQty);

    /**
     * 查询指定格口上最大可用库存的记录（FIFO：先进先出）
     */
    @Select("SELECT id, shelf_id, floor_number, quantity, locked_quantity, " +
            "  (quantity - COALESCE(locked_quantity, 0)) AS available_quantity " +
            "FROM stock_in_record " +
            "WHERE product_id = #{productId} AND shelf_id = #{shelfId} AND floor_number = #{floorNumber} " +
            "  AND (quantity - COALESCE(locked_quantity, 0)) >= #{requiredQty} " +
            "ORDER BY storage_time ASC LIMIT 1")
    Map<String, Object> findStockSlotFIFO(@Param("productId") Integer productId,
                                           @Param("shelfId") Integer shelfId,
                                           @Param("floorNumber") Integer floorNumber,
                                           @Param("requiredQty") Integer requiredQty);

    /**
     * 扣减实际库存：减少 quantity（出库时调用）
     */
    @Update("UPDATE stock_in_record SET quantity = quantity - #{qty} WHERE id = #{recordId}")
    int decreaseQuantity(@Param("recordId") Integer recordId, @Param("qty") Integer qty);
}

