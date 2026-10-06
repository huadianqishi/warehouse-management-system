package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址簿Mapper
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
