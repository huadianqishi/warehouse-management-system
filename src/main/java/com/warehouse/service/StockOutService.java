package com.warehouse.service;

import com.warehouse.entity.StockOutRecord;

/**
 * 出库服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface StockOutService {

    /**
     * 添加出库记录
     *
     * @param stockOutRecord 出库记录
     * @param operatorId 操作人ID
     */
    void addStockOut(StockOutRecord stockOutRecord, Integer operatorId);
}
