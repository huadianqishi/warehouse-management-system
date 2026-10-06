package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 仓库Mapper接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {

    /**
     * 检查仓库下是否有货架
     */
    @Select("SELECT COUNT(*) FROM shelf WHERE warehouse_id = #{warehouseId}")
    Integer countShelfByWarehouseId(Integer warehouseId);
}

