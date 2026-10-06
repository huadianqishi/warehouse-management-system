package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.entity.OrderDetail;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.OrderDetailMapper;
import com.warehouse.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单商品关联服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrderDetail(OrderDetail detail, Integer userId) {
        if (detail.getOrderId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (detail.getProductId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
            throw new BusinessException("发货数量必须大于0");
        }
        orderDetailMapper.insert(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrderDetail(Integer id, Integer userId) {
        OrderDetail detail = orderDetailMapper.selectById(id);
        if (detail == null) {
            throw new BusinessException("订单商品记录不存在");
        }
        orderDetailMapper.deleteById(id);
    }

    @Override
    public List<OrderDetail> getDetailsByOrderId(Integer orderId) {
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        return orderDetailMapper.selectList(wrapper);
    }
}
