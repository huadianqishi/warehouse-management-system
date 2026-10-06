package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车辆Mapper
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {
}
