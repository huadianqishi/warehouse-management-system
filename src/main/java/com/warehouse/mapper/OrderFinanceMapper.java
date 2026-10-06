package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.OrderFinance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单费用主表Mapper
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface OrderFinanceMapper extends BaseMapper<OrderFinance> {
}
