package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Shelf;
import org.apache.ibatis.annotations.Mapper;

/**
 * 货架Mapper接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface ShelfMapper extends BaseMapper<Shelf> {

    /**
     * 检查货架上是否有库存记录（入库记录）
     * 注意：此方法用于检查货架是否被使用，实际应检查stock_in_record表
     * 由于stock_in_record属于库存管理模块，这里暂时保留方法签名
     */
    // @Select("SELECT COUNT(*) FROM stock_in_record WHERE shelf_id = #{shelfId}")
    // Integer countStockRecordByShelfId(Integer shelfId);
}

