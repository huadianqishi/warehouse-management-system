package com.warehouse.service;

import com.warehouse.entity.AddressBook;

import java.util.List;

/**
 * 地址簿服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface AddressBookService {

    /**
     * 新增地址
     */
    void addAddress(AddressBook address, Integer userId);

    /**
     * 删除地址
     */
    void deleteAddress(Integer id, Integer userId);

    /**
     * 更新地址信息
     */
    void updateAddress(AddressBook address, Integer userId);

    /**
     * 根据ID查询地址
     */
    AddressBook getAddressById(Integer id);

    /**
     * 查询所有地址（支持按用户筛选）
     */
    List<AddressBook> getAllAddresses(Integer userId);

    /**
     * 按类型查询地址（0-发货人, 1-收货人）
     */
    List<AddressBook> getAddressesByType(Integer type);
}
