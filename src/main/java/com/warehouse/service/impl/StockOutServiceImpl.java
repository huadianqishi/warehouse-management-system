package com.warehouse.service.impl;

import com.warehouse.entity.StockOutRecord;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.StockOutRecordMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.StockOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 出库服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Slf4j
@Service
public class StockOutServiceImpl implements StockOutService {

    @Autowired
    private StockOutRecordMapper stockOutRecordMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStockOut(StockOutRecord stockOutRecord, Integer operatorId) {
        log.info("开始执行出库操作，参数：productId={}, quantity={}, shelfId={}, floorNumber={}, operatorId={}",
            stockOutRecord.getProductId(),
            stockOutRecord.getQuantity(),
            stockOutRecord.getShelfId(),
            stockOutRecord.getFloorNumber(),
            operatorId);

        if (stockOutRecord.getProductId() == null) {
            log.error("商品ID为空");
            throw new BusinessException("商品ID不能为空");
        }
        if (stockOutRecord.getQuantity() == null || stockOutRecord.getQuantity() <= 0) {
            log.error("出库数量无效：{}", stockOutRecord.getQuantity());
            throw new BusinessException("出库数量必须大于0");
        }
        if (stockOutRecord.getShelfId() == null) {
            log.error("货架ID为空");
            throw new BusinessException("货架ID不能为空");
        }
        if (stockOutRecord.getFloorNumber() == null || stockOutRecord.getFloorNumber() <= 0) {
            log.error("层数无效：{}", stockOutRecord.getFloorNumber());
            throw new BusinessException("层数必须大于0");
        }

        // 检查库存是否充足
        Integer currentStock = stockOutRecordMapper.getSlotCurrentQuantity(
            stockOutRecord.getShelfId(),
            stockOutRecord.getFloorNumber(),
            stockOutRecord.getProductId()
        );
        if (currentStock == null) {
            currentStock = 0;
        }
        if (currentStock < stockOutRecord.getQuantity()) {
            log.error("库存不足：当前库存={}，请求出库={}", currentStock, stockOutRecord.getQuantity());
            throw new BusinessException("库存不足，当前库存为 " + currentStock + "，无法出库 " + stockOutRecord.getQuantity());
        }

        if (stockOutRecord.getOutTime() == null) {
            stockOutRecord.setOutTime(new Date());
        }
        if (stockOutRecord.getOperatorId() == null) {
            stockOutRecord.setOperatorId(operatorId);
        }

        log.info("准备插入出库记录到数据库...");
        int result = stockOutRecordMapper.insert(stockOutRecord);
        log.info("插入结果：result={}, 生成的ID={}", result, stockOutRecord.getId());

        if (result <= 0) {
            log.error("插入出库记录失败，result={}", result);
            throw new BusinessException("添加出库记录失败");
        }

        log.info("出库记录插入成功，记录ID：{}", stockOutRecord.getId());

        // 记录操作日志（操作类型ID=15：执行出库操作）
        try {
            operationLogService.logOperation(operatorId, 15, "成功", "stock_out_record",
                stockOutRecord.getId(),
                String.format("执行出库操作：商品ID=%d，数量=%d，货架=%d，层数=%d，去向=%s",
                    stockOutRecord.getProductId(),
                    stockOutRecord.getQuantity(),
                    stockOutRecord.getShelfId(),
                    stockOutRecord.getFloorNumber(),
                    stockOutRecord.getDestination() == null ? "未填写" : stockOutRecord.getDestination()));
            log.info("操作日志记录成功");
        } catch (Exception e) {
            log.error("记录操作日志失败，但不影响出库操作：", e);
        }

        log.info("出库操作完成");
    }
}
