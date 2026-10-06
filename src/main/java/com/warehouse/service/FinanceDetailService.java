package com.warehouse.service;

import com.warehouse.entity.FinanceDetail;

import java.util.List;

/**
 * 费用明细服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface FinanceDetailService {

    /**
     * 添加费用明细
     */
    void addFinanceDetail(FinanceDetail detail, Integer userId);

    /**
     * 删除费用明细
     */
    void deleteFinanceDetail(Integer id, Integer userId);

    /**
     * 根据费用主表ID查询明细列表
     */
    List<FinanceDetail> getDetailsByFinanceId(Integer financeId);
}
