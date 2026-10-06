package com.warehouse.service;

import com.warehouse.entity.OrderDetail;

import java.util.List;

/**
 * 订单商品关联服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface OrderDetailService {

    /**
     * 添加订单商品
     */
    void addOrderDetail(OrderDetail detail, Integer userId);

    /**
     * 删除订单商品
     */
    void deleteOrderDetail(Integer id, Integer userId);

    /**
     * 根据订单ID查询商品列表
     */
    List<OrderDetail> getDetailsByOrderId(Integer orderId);
}
