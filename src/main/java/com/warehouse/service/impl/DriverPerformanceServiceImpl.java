package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.entity.DriverPerformance;
import com.warehouse.entity.LogisticsOrder;
import com.warehouse.entity.OrderFinance;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.DriverPerformanceMapper;
import com.warehouse.mapper.LogisticsOrderMapper;
import com.warehouse.mapper.OrderFinanceMapper;
import com.warehouse.service.DriverPerformanceService;
import com.warehouse.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 司机绩效服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class DriverPerformanceServiceImpl implements DriverPerformanceService {

    @Autowired
    private DriverPerformanceMapper driverPerformanceMapper;

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    @Autowired
    private OrderFinanceMapper orderFinanceMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordOrderCompletion(Integer driverId, Integer orderId, Integer userId) {
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        String month = order.getActualArrivalTime() != null
                ? order.getActualArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        QueryWrapper<DriverPerformance> wrapper = new QueryWrapper<>();
        wrapper.eq("driver_id", driverId).eq("month", month);
        DriverPerformance performance = driverPerformanceMapper.selectOne(wrapper);

        if (performance == null) {
            performance = new DriverPerformance();
            performance.setDriverId(driverId);
            performance.setMonth(month);
            performance.setOrderCount(1);
            OrderFinance finance = orderFinanceMapper.selectOne(
                    new QueryWrapper<OrderFinance>().eq("order_id", orderId));
            performance.setTotalProfit(finance != null ? finance.getActualProfit() : BigDecimal.ZERO);
            performance.setLastUpdateTime(LocalDateTime.now());
            driverPerformanceMapper.insert(performance);
        } else {
            performance.setOrderCount(performance.getOrderCount() + 1);
            OrderFinance finance = orderFinanceMapper.selectOne(
                    new QueryWrapper<OrderFinance>().eq("order_id", orderId));
            if (finance != null) {
                performance.setTotalProfit(
                        performance.getTotalProfit().add(finance.getActualProfit()));
            }
            performance.setLastUpdateTime(LocalDateTime.now());
            driverPerformanceMapper.updateById(performance);
        }
    }

    @Override
    public List<DriverPerformance> getPerformanceByDriverId(Integer driverId) {
        QueryWrapper<DriverPerformance> wrapper = new QueryWrapper<>();
        wrapper.eq("driver_id", driverId).orderByDesc("month");
        return driverPerformanceMapper.selectList(wrapper);
    }

    @Override
    public List<DriverPerformance> getPerformanceByMonth(String month) {
        QueryWrapper<DriverPerformance> wrapper = new QueryWrapper<>();
        wrapper.eq("month", month).orderByDesc("total_profit");
        return driverPerformanceMapper.selectList(wrapper);
    }

    @Override
    public DriverPerformance getDriverMonthPerformance(Integer driverId, String month) {
        QueryWrapper<DriverPerformance> wrapper = new QueryWrapper<>();
        wrapper.eq("driver_id", driverId).eq("month", month);
        return driverPerformanceMapper.selectOne(wrapper);
    }
}
