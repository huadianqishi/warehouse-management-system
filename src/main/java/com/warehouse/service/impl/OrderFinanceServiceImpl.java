package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.entity.FinanceDetail;
import com.warehouse.entity.LogisticsOrder;
import com.warehouse.entity.OrderFinance;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.FinanceDetailMapper;
import com.warehouse.mapper.LogisticsOrderMapper;
import com.warehouse.mapper.OrderFinanceMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.OrderFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单费用服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class OrderFinanceServiceImpl implements OrderFinanceService {

    @Autowired
    private OrderFinanceMapper orderFinanceMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private FinanceDetailMapper financeDetailMapper;

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrderFinance(OrderFinance finance, Integer userId) {
        if (finance.getOrderId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        QueryWrapper<OrderFinance> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", finance.getOrderId());
        if (orderFinanceMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该订单已存在费用记录");
        }
        // 校验订单状态：只有已完成的订单才可创建费用记录
        LogisticsOrder order = logisticsOrderMapper.selectById(finance.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 3) {
            throw new BusinessException("只有已完成的订单才能创建费用记录");
        }
        // 快照下单时的订单状态
        finance.setOrderStatus(order.getStatus());
        if (finance.getTotalRevenue() == null) {
            finance.setTotalRevenue(BigDecimal.ZERO);
        }
        if (finance.getTotalCost() == null) {
            finance.setTotalCost(BigDecimal.ZERO);
        }
        if (finance.getActualProfit() == null) {
            finance.setActualProfit(finance.getTotalRevenue().subtract(finance.getTotalCost()));
        }
        if (finance.getSettlementStatus() == null) {
            finance.setSettlementStatus(0);
        }
        finance.setUpdateTime(LocalDateTime.now());
        orderFinanceMapper.insert(finance);
        // 记录操作日志（操作类型ID=31：录入运费收入）
        operationLogService.logOperation(userId, 31, "成功", "order_finance", finance.getId(),
                "录入运费收入：订单ID=" + finance.getOrderId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderFinance(OrderFinance finance, Integer userId) {
        OrderFinance old = orderFinanceMapper.selectById(finance.getId());
        if (old == null) {
            throw new BusinessException("费用记录不存在");
        }
        if (old.getSettlementStatus() != null && old.getSettlementStatus() == 2) {
            throw new BusinessException("已结算的订单无法修改费用");
        }
        finance.setActualProfit(finance.getTotalRevenue().subtract(finance.getTotalCost()));
        finance.setUpdateTime(LocalDateTime.now());
        orderFinanceMapper.updateById(finance);
    }

    @Override
    public OrderFinance getFinanceByOrderId(Integer orderId) {
        QueryWrapper<OrderFinance> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        return orderFinanceMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitForAudit(Integer orderId, Integer userId) {
        OrderFinance finance = getFinanceByOrderId(orderId);
        if (finance == null) {
            throw new BusinessException("费用记录不存在");
        }
        if (finance.getSettlementStatus() != 0) {
            throw new BusinessException("只能对待结订单提交审核，当前状态：" + getSettlementStatusText(finance.getSettlementStatus()));
        }
        // 校验订单状态：已取消的订单不能提交审核
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order != null && order.getStatus() != null && order.getStatus() == 4) {
            throw new BusinessException("已取消的订单不能提交结算审核");
        }
        finance.setSettlementStatus(1);
        finance.setUpdateTime(LocalDateTime.now());
        orderFinanceMapper.updateById(finance);
        // 记录操作日志（操作类型ID=33：提交结算审核）
        operationLogService.logOperation(userId, 33, "成功", "order_finance", orderId,
                "提交结算审核：订单ID=" + orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmSettle(Integer orderId, Integer userId) {
        OrderFinance finance = getFinanceByOrderId(orderId);
        if (finance == null) {
            throw new BusinessException("费用记录不存在");
        }
        if (finance.getSettlementStatus() != 1) {
            throw new BusinessException("只能对待审订单进行终审，当前状态：" + getSettlementStatusText(finance.getSettlementStatus()));
        }
        // 终审前重新汇总明细
        recalculateProfit(finance.getId(), userId);
        // 再次查询确保数据最新
        finance = orderFinanceMapper.selectById(finance.getId());
        // 校验订单状态：已取消的订单不能结算
        LogisticsOrder order = logisticsOrderMapper.selectById(orderId);
        if (order != null && order.getStatus() != null && order.getStatus() == 4) {
            throw new BusinessException("已取消的订单不能确认结算");
        }
        finance.setSettlementStatus(2);
        finance.setUpdateTime(LocalDateTime.now());
        orderFinanceMapper.updateById(finance);
        // 记录操作日志（操作类型ID=34：确认订单结算）
        operationLogService.logOperation(userId, 34, "成功", "order_finance", orderId,
                "确认订单结算：订单ID=" + orderId);
    }

    @Override
    public List<OrderFinance> getFinancesByStatus(Integer settlementStatus) {
        QueryWrapper<OrderFinance> wrapper = new QueryWrapper<>();
        if (settlementStatus != null) {
            wrapper.eq("settlement_status", settlementStatus);
        }
        return orderFinanceMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recalculateProfit(Integer financeId, Integer userId) {
        OrderFinance finance = orderFinanceMapper.selectById(financeId);
        if (finance == null) {
            throw new BusinessException("费用记录不存在");
        }
        if (finance.getSettlementStatus() != null && finance.getSettlementStatus() == 2) {
            throw new BusinessException("已结算的订单无法重新汇总");
        }

        QueryWrapper<FinanceDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("finance_id", financeId);
        List<FinanceDetail> details = financeDetailMapper.selectList(detailWrapper);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        for (FinanceDetail detail : details) {
            if (detail.getDirection() != null && detail.getDirection() == 1) {
                totalRevenue = totalRevenue.add(detail.getAmount());
            } else {
                totalCost = totalCost.add(detail.getAmount());
            }
        }

        finance.setTotalRevenue(totalRevenue);
        finance.setTotalCost(totalCost);
        finance.setActualProfit(totalRevenue.subtract(totalCost));
        finance.setUpdateTime(LocalDateTime.now());
        orderFinanceMapper.updateById(finance);
    }

    @Override
    public BigDecimal calculateActualProfit(Integer orderId) {
        OrderFinance finance = getFinanceByOrderId(orderId);
        if (finance == null) {
            return BigDecimal.ZERO;
        }
        return finance.getTotalRevenue().subtract(finance.getTotalCost());
    }

    private String getSettlementStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "未结";
            case 1: return "待审";
            case 2: return "已结";
            default: return "未知";
        }
    }
}
