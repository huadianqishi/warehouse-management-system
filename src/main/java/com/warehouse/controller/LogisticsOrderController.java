package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.LogisticsOrder;
import com.warehouse.entity.dto.CreateLogisticsOrderDTO;
import com.warehouse.entity.dto.LogisticsOrderVO;
import com.warehouse.service.LogisticsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 物流订单控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/logistics")
public class LogisticsOrderController {

    @Autowired
    private LogisticsOrderService logisticsOrderService;

    /**
     * 创建物流订单
     * 自动根据商品明细计算总重量、总体积、货值，并按收货地址计算预估送达时间
     */
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody CreateLogisticsOrderDTO dto, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        logisticsOrderService.createOrder(dto, userId);
        return Result.success("创建订单成功");
    }

    /**
     * 取消物流订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(@PathVariable Integer orderId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        logisticsOrderService.cancelOrder(orderId, userId);
        return Result.success("取消订单成功");
    }

    /**
     * 指派司机和车辆
     */
    @PostMapping("/assign")
    public Result<String> assignDriverAndVehicle(@RequestParam Integer orderId,
                                                 @RequestParam Integer driverId,
                                                 @RequestParam Integer vehicleId,
                                                 HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        logisticsOrderService.assignDriverAndVehicle(orderId, driverId, vehicleId, userId);
        return Result.success("指派成功");
    }

    /**
     * 司机确认出发，开始运输
     */
    @PostMapping("/start/{orderId}")
    public Result<String> startDelivery(@PathVariable Integer orderId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        logisticsOrderService.startDelivery(orderId, userId);
        return Result.success("开始运输");
    }

    /**
     * 司机确认送达，完成订单
     * 自动创建财务记录（未结），并更新司机月度绩效
     */
    @PostMapping("/complete/{orderId}")
    public Result<String> completeOrder(@PathVariable Integer orderId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        logisticsOrderService.completeOrder(orderId, userId);
        return Result.success("确认送达成功");
    }

    /**
     * 根据ID查询订单（基础信息）
     */
    @GetMapping("/get/{id}")
    public Result<LogisticsOrder> getOrderById(@PathVariable Integer id) {
        return Result.success(logisticsOrderService.getOrderById(id));
    }

    /**
     * 查询所有订单（基础信息，支持按状态筛选）
     */
    @GetMapping("/list")
    public Result<List<LogisticsOrder>> getAllOrders(@RequestParam(required = false) Integer status) {
        return Result.success(logisticsOrderService.getAllOrders(status));
    }

    /**
     * 根据运单号查询（基础信息）
     */
    @GetMapping("/get-by-no")
    public Result<LogisticsOrder> getOrderByNo(@RequestParam String orderNo) {
        return Result.success(logisticsOrderService.getOrderByNo(orderNo));
    }

    /**
     * 获取订单完整视图（含发货人、收货人、司机、车辆、商品明细）
     */
    @GetMapping("/vo/{id}")
    public Result<LogisticsOrderVO> getOrderVO(@PathVariable Integer id) {
        return Result.success(logisticsOrderService.getOrderVO(id));
    }

    /**
     * 获取订单完整视图列表（支持按状态筛选）
     */
    @GetMapping("/vo/list")
    public Result<List<LogisticsOrderVO>> getAllOrderVOs(@RequestParam(required = false) Integer status) {
        return Result.success(logisticsOrderService.getAllOrderVOs(status));
    }
}
