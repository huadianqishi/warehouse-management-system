package com.warehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.entity.User;
import com.warehouse.entity.Vehicle;
import com.warehouse.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 车辆控制器
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/add")
    public Result<String> addVehicle(@RequestBody Vehicle vehicle, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        vehicleService.addVehicle(vehicle, userId);
        return Result.success("添加车辆成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteVehicle(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        vehicleService.deleteVehicle(id, userId);
        return Result.success("删除车辆成功");
    }

    @PutMapping("/update")
    public Result<String> updateVehicle(@RequestBody Vehicle vehicle, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        vehicleService.updateVehicle(vehicle, userId);
        return Result.success("修改车辆成功");
    }

    @GetMapping("/get/{id}")
    public Result<Vehicle> getVehicleById(@PathVariable Integer id) {
        return Result.success(vehicleService.getVehicleById(id));
    }

    @GetMapping("/list")
    public Result<List<Vehicle>> getAllVehicles() {
        return Result.success(vehicleService.getAllVehicles());
    }

    /**
     * 分页查询车辆（支持按车型筛选）
     */
    @GetMapping("/page")
    public Result<IPage<Vehicle>> getVehiclePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "6") Long size,
            @RequestParam(required = false) Integer category) {
        return Result.success(vehicleService.getVehiclePage(current, size, category));
    }

    /**
     * 按车牌号搜索车辆
     */
    @GetMapping("/search")
    public Result<List<Vehicle>> searchVehicles(@RequestParam String keyword) {
        return Result.success(vehicleService.searchVehicles(keyword));
    }

    /**
     * 查询可用车辆（按载重/容积筛选，结果按容量最接近排序推荐）
     */
    @GetMapping("/available")
    public Result<List<Vehicle>> getAvailableVehicles(
            @RequestParam(required = false) BigDecimal weight,
            @RequestParam(required = false) BigDecimal volume) {
        return Result.success(vehicleService.getAvailableVehicles(weight, volume, volume));
    }

    @PostMapping("/bind-driver")
    public Result<String> bindDriver(@RequestParam Integer vehicleId,
                                     @RequestParam Integer driverId,
                                     HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        vehicleService.bindDriver(vehicleId, driverId, userId);
        return Result.success("绑定司机成功");
    }

    @PostMapping("/unbind-driver")
    public Result<String> unbindDriver(@RequestParam Integer vehicleId,
                                        HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        vehicleService.unbindDriver(vehicleId, userId);
        return Result.success("解绑司机成功");
    }

    /**
     * 查询所有空闲司机（role=3且已录入驾驶证）
     */
    @GetMapping("/drivers/available")
    public Result<List<User>> getAvailableDrivers() {
        return Result.success(vehicleService.getAvailableDrivers());
    }

    /**
     * 查询所有司机
     */
    @GetMapping("/drivers/all")
    public Result<List<User>> getAllDrivers() {
        return Result.success(vehicleService.getAllDrivers());
    }

    /**
     * 查询可用司机+车辆捆绑列表（仅返回有绑定车辆且车辆空闲的司机）
     */
    @GetMapping("/driver-vehicles")
    public Result<List<com.warehouse.entity.dto.DriverVehicleDTO>> getAvailableDriverVehicles(
            @RequestParam(required = false) BigDecimal weight,
            @RequestParam(required = false) BigDecimal volume) {
        return Result.success(vehicleService.getAvailableDriverVehicles(weight, volume, volume));
    }
}
