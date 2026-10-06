package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper接口
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}

