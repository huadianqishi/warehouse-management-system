package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.entity.FinanceDetail;
import com.warehouse.entity.OrderFinance;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.FinanceDetailMapper;
import com.warehouse.mapper.OrderFinanceMapper;
import com.warehouse.service.FinanceDetailService;
import com.warehouse.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 费用明细服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class FinanceDetailServiceImpl implements FinanceDetailService {

    @Autowired
    private FinanceDetailMapper financeDetailMapper;

    @Autowired
    private OrderFinanceMapper orderFinanceMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFinanceDetail(FinanceDetail detail, Integer userId) {
        if (detail.getFinanceId() == null) {
            throw new BusinessException("费用主表ID不能为空");
        }
        if (detail.getItemName() == null || detail.getItemName().trim().isEmpty()) {
            throw new BusinessException("项目名称不能为空");
        }
        if (detail.getAmount() == null || detail.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("金额不能为零");
        }
        if (detail.getDirection() == null) {
            throw new BusinessException("收支方向不能为空");
        }

        OrderFinance finance = orderFinanceMapper.selectById(detail.getFinanceId());
        if (finance == null) {
            throw new BusinessException("费用记录不存在");
        }
        if (finance.getSettlementStatus() != null && finance.getSettlementStatus() == 2) {
            throw new BusinessException("已结算的订单无法添加费用");
        }

        financeDetailMapper.insert(detail);

        // 自动重新汇总父表
        recalculateParentFinance(detail.getFinanceId());
        
        // 记录操作日志
        int operationTypeId = (detail.getDirection() != null && detail.getDirection() == 1) ? 31 : 32;
        String operationName = (detail.getDirection() != null && detail.getDirection() == 1) ? "录入运费收入" : "录入费用支出";
        operationLogService.logOperation(userId, operationTypeId, "成功", "finance_detail", detail.getId(),
                operationName + "：金额=" + detail.getAmount() + "，项目=" + detail.getItemName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFinanceDetail(Integer id, Integer userId) {
        FinanceDetail detail = financeDetailMapper.selectById(id);
        if (detail == null) {
            throw new BusinessException("费用明细不存在");
        }
        Integer financeId = detail.getFinanceId();

        OrderFinance finance = orderFinanceMapper.selectById(financeId);
        if (finance != null && finance.getSettlementStatus() != null && finance.getSettlementStatus() == 2) {
            throw new BusinessException("已结算的订单无法删除费用");
        }

        financeDetailMapper.deleteById(id);

        // 自动重新汇总父表
        recalculateParentFinance(financeId);
    }

    @Override
    public List<FinanceDetail> getDetailsByFinanceId(Integer financeId) {
        QueryWrapper<FinanceDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("finance_id", financeId);
        return financeDetailMapper.selectList(wrapper);
    }

    /**
     * 重新汇总指定费用主表下的所有明细，更新 revenue/cost/profit
     */
    private void recalculateParentFinance(Integer financeId) {
        OrderFinance finance = orderFinanceMapper.selectById(financeId);
        if (finance == null) return;

        QueryWrapper<FinanceDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("finance_id", financeId);
        List<FinanceDetail> details = financeDetailMapper.selectList(wrapper);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        for (FinanceDetail d : details) {
            if (d.getDirection() != null && d.getDirection() == 1) {
                totalRevenue = totalRevenue.add(d.getAmount());
            } else {
                totalCost = totalCost.add(d.getAmount());
            }
        }

        finance.setTotalRevenue(totalRevenue);
        finance.setTotalCost(totalCost);
        finance.setActualProfit(totalRevenue.subtract(totalCost));
        finance.setUpdateTime(LocalDateTime.now());
        orderFinanceMapper.updateById(finance);
    }
}
