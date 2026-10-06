package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.AddressBook;
import com.warehouse.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 地址簿控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/address-book")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/add")
    public Result<String> addAddress(@RequestBody AddressBook address, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        addressBookService.addAddress(address, userId);
        return Result.success("添加地址成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteAddress(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        addressBookService.deleteAddress(id, userId);
        return Result.success("删除地址成功");
    }

    @PutMapping("/update")
    public Result<String> updateAddress(@RequestBody AddressBook address, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        addressBookService.updateAddress(address, userId);
        return Result.success("修改地址成功");
    }

    @GetMapping("/get/{id}")
    public Result<AddressBook> getAddressById(@PathVariable Integer id) {
        return Result.success(addressBookService.getAddressById(id));
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> getAllAddresses(
            @RequestParam(required = false) Integer userId) {
        return Result.success(addressBookService.getAllAddresses(userId));
    }

    @GetMapping("/list-by-type")
    public Result<List<AddressBook>> getAddressesByType(@RequestParam Integer type) {
        return Result.success(addressBookService.getAddressesByType(type));
    }
}
