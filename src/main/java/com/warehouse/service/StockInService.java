package com.warehouse.service;

import com.warehouse.entity.StockInRecord;

/**
 * 入库服务接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface StockInService {

    /**
     * 添加入库记录
     * 
     * @param stockInRecord 入库记录
     * @param operatorId 操作人ID
     */
    void addStockIn(StockInRecord stockInRecord, Integer operatorId);
}

