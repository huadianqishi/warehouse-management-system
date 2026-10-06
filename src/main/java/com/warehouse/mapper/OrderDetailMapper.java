package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品关联Mapper
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
