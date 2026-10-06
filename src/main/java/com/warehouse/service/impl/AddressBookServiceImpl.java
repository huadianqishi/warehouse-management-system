package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.entity.AddressBook;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.AddressBookMapper;
import com.warehouse.service.AddressBookService;
import com.warehouse.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址簿服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(AddressBook address, Integer userId) {
        if (address.getContactName() == null || address.getContactName().trim().isEmpty()) {
            throw new BusinessException("联系人姓名不能为空");
        }
        if (address.getPhone() == null || address.getPhone().trim().isEmpty()) {
            throw new BusinessException("联系电话不能为空");
        }
        if (address.getProvince() == null || address.getProvince().trim().isEmpty()) {
            throw new BusinessException("省份不能为空");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new BusinessException("城市不能为空");
        }
        if (address.getDetailAddress() == null || address.getDetailAddress().trim().isEmpty()) {
            throw new BusinessException("详细地址不能为空");
        }
        if (address.getType() == null) {
            address.setType(0);
        }
        int result = addressBookMapper.insert(address);
        if (result <= 0) {
            throw new BusinessException("添加地址失败");
        }
        operationLogService.logOperation(userId, 28, "成功", "address_book", address.getId(),
                "添加地址：" + address.getContactName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Integer id, Integer userId) {
        AddressBook address = addressBookMapper.selectById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        int result = addressBookMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除地址失败");
        }
        operationLogService.logOperation(userId, 29, "成功", "address_book", id,
                "删除地址：" + address.getContactName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(AddressBook address, Integer userId) {
        AddressBook old = addressBookMapper.selectById(address.getId());
        if (old == null) {
            throw new BusinessException("地址不存在");
        }
        int result = addressBookMapper.updateById(address);
        if (result <= 0) {
            throw new BusinessException("修改地址失败");
        }
        operationLogService.logOperation(userId, 30, "成功", "address_book", address.getId(),
                "修改地址信息");
    }

    @Override
    public AddressBook getAddressById(Integer id) {
        AddressBook address = addressBookMapper.selectById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    @Override
    public List<AddressBook> getAllAddresses(Integer userId) {
        QueryWrapper<AddressBook> wrapper = new QueryWrapper<>();
        if (userId != null) {
            wrapper.eq("user_id", userId).or().isNull("user_id");
        }
        return addressBookMapper.selectList(wrapper);
    }

    @Override
    public List<AddressBook> getAddressesByType(Integer type) {
        QueryWrapper<AddressBook> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        return addressBookMapper.selectList(wrapper);
    }
}
