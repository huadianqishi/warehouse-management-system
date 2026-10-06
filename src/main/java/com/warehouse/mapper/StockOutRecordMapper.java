package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.StockOutRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 出库记录Mapper接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface StockOutRecordMapper extends BaseMapper<StockOutRecord> {

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
}
