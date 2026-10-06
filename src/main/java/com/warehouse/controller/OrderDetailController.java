package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.OrderDetail;
import com.warehouse.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单商品关联控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/order-detail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/add")
    public Result<String> addOrderDetail(@RequestBody OrderDetail detail, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderDetailService.addOrderDetail(detail, userId);
        return Result.success("添加订单商品成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteOrderDetail(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderDetailService.deleteOrderDetail(id, userId);
        return Result.success("删除订单商品成功");
    }

    @GetMapping("/list/{orderId}")
    public Result<List<OrderDetail>> getDetailsByOrderId(@PathVariable Integer orderId) {
        return Result.success(orderDetailService.getDetailsByOrderId(orderId));
    }
}
