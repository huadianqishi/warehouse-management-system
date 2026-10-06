package com.warehouse.service;

import com.warehouse.entity.OrderFinance;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单费用服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface OrderFinanceService {

    /**
     * 创建订单费用记录
     */
    void createOrderFinance(OrderFinance finance, Integer userId);

    /**
     * 更新订单费用
     */
    void updateOrderFinance(OrderFinance finance, Integer userId);

    /**
     * 根据订单ID查询费用
     */
    OrderFinance getFinanceByOrderId(Integer orderId);

    /**
     * 提交结算申请（0-未结 → 1-待审）
     */
    void submitForAudit(Integer orderId, Integer userId);

    /**
     * 财务终审确认结算（1-待审 → 2-已结）
     */
    void confirmSettle(Integer orderId, Integer userId);

    /**
     * 查询指定结算状态的订单费用列表
     * settlementStatus: 0-未结, 1-待审, 2-已结
     */
    List<OrderFinance> getFinancesByStatus(Integer settlementStatus);

    /**
     * 根据费用主表ID重新汇总所有明细，重新计算 actual_profit
     */
    void recalculateProfit(Integer financeId, Integer userId);

    /**
     * 计算某订单的实际利润
     */
    BigDecimal calculateActualProfit(Integer orderId);
}
