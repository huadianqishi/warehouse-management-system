package com.warehouse.service.impl;

import com.warehouse.entity.StockInRecord;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.StockInRecordMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.StockInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 入库服务实现类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Slf4j
@Service
public class StockInServiceImpl implements StockInService {

    @Autowired
    private StockInRecordMapper stockInRecordMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStockIn(StockInRecord stockInRecord, Integer operatorId) {
        log.info("开始执行入库操作，参数：productId={}, quantity={}, shelfId={}, floorNumber={}, operatorId={}", 
            stockInRecord.getProductId(), 
            stockInRecord.getQuantity(), 
            stockInRecord.getShelfId(), 
            stockInRecord.getFloorNumber(),
            operatorId);
        
        // 验证必填字段
        if (stockInRecord.getProductId() == null) {
            log.error("商品ID为空");
            throw new BusinessException("商品ID不能为空");
        }
        if (stockInRecord.getQuantity() == null || stockInRecord.getQuantity() <= 0) {
            log.error("入库数量无效：{}", stockInRecord.getQuantity());
            throw new BusinessException("入库数量必须大于0");
        }
        if (stockInRecord.getShelfId() == null) {
            log.error("货架ID为空");
            throw new BusinessException("货架ID不能为空");
        }
        if (stockInRecord.getFloorNumber() == null || stockInRecord.getFloorNumber() <= 0) {
            log.error("层数无效：{}", stockInRecord.getFloorNumber());
            throw new BusinessException("层数必须大于0");
        }
        if (stockInRecord.getStorageTime() == null) {
            stockInRecord.setStorageTime(new Date());
            log.info("设置入库时间为：{}", stockInRecord.getStorageTime());
        }
        if (stockInRecord.getOperatorId() == null) {
            stockInRecord.setOperatorId(operatorId);
            log.info("设置操作人ID为：{}", operatorId);
        }

        log.info("准备插入入库记录到数据库...");
        // 插入入库记录
        int result = stockInRecordMapper.insert(stockInRecord);
        log.info("插入结果：result={}, 生成的ID={}", result, stockInRecord.getId());
        
        if (result <= 0) {
            log.error("插入入库记录失败，result={}", result);
            throw new BusinessException("添加入库记录失败");
        }
        
        log.info("入库记录插入成功，记录ID：{}", stockInRecord.getId());

        // 记录操作日志（操作类型ID=14：执行入库操作）
        try {
            operationLogService.logOperation(operatorId, 14, "成功", "stock_in_record", 
                stockInRecord.getId(), 
                String.format("执行入库操作：商品ID=%d，数量=%d，货架=%d，层数=%d", 
                    stockInRecord.getProductId(), 
                    stockInRecord.getQuantity(),
                    stockInRecord.getShelfId(),
                    stockInRecord.getFloorNumber()));
            log.info("操作日志记录成功");
        } catch (Exception e) {
            log.error("记录操作日志失败，但不影响入库操作：", e);
            // 操作日志失败不影响入库操作
        }
        
        log.info("入库操作完成");
    }
}

