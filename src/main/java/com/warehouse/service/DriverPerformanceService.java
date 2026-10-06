package com.warehouse.service;

import com.warehouse.entity.DriverPerformance;

import java.util.List;

/**
 * 司机绩效服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface DriverPerformanceService {

    /**
     * 记录司机完成订单（更新绩效）
     */
    void recordOrderCompletion(Integer driverId, Integer orderId, Integer userId);

    /**
     * 根据司机ID查询绩效
     */
    List<DriverPerformance> getPerformanceByDriverId(Integer driverId);

    /**
     * 查询某月所有司机绩效
     */
    List<DriverPerformance> getPerformanceByMonth(String month);

    /**
     * 获取司机月度统计
     */
    DriverPerformance getDriverMonthPerformance(Integer driverId, String month);
}
