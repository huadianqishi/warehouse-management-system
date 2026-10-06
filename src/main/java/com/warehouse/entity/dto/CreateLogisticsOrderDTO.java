package com.warehouse.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建物流订单请求DTO
 * 用于接收前端创建订单时的完整数据（发货人、收货人、商品明细列表）
 *
 * @author AI助手
 */
@Data
public class CreateLogisticsOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发货地址ID（关联address_book）
     */
    private Integer senderId;

    /**
     * 收货地址ID（关联address_book）
     */
    private Integer receiverId;

    /**
     * 订单商品明细列表
     */
    private java.util.List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 商品ID
         */
        private Integer productId;

        /**
         * 发货数量
         */
        private Integer quantity;

        /**
         * 对应入库批次号（可选）
         */
        private String batchNo;
    }
}
