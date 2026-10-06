package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.LogisticsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 物流订单Mapper
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface LogisticsOrderMapper extends BaseMapper<LogisticsOrder> {

    /**
     * 按车型聚合某司机的已完成订单实际利润
     * JOIN logistics_order + order_finance（取真实利润）+ vehicle（取车型）
     */
    @Select("SELECT v.category, SUM(f.actual_profit) AS total_profit, COUNT(*) AS order_count " +
            "FROM logistics_order o " +
            "INNER JOIN order_finance f ON o.id = f.order_id " +
            "INNER JOIN vehicle v ON o.vehicle_id = v.id " +
            "WHERE o.driver_id = #{driverId} AND o.status = 3 AND o.vehicle_id IS NOT NULL " +
            "GROUP BY v.category")
    List<Map<String, Object>> selectVehicleProfitGroupByDriver(@Param("driverId") Integer driverId);

    /**
     * 清空指定车辆的订单关联（解除外键引用，以便删除车辆）
     */
    @Select("UPDATE logistics_order SET vehicle_id = NULL, driver_id = NULL WHERE vehicle_id = #{vehicleId}")
    void clearVehicleRelation(@Param("vehicleId") Integer vehicleId);
}
