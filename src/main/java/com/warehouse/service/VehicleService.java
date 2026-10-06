package com.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.warehouse.entity.User;
import com.warehouse.entity.Vehicle;
import com.warehouse.entity.dto.DriverVehicleDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 车辆服务接口
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
public interface VehicleService {

    /**
     * 新增车辆
     */
    void addVehicle(Vehicle vehicle, Integer userId);

    /**
     * 删除车辆
     */
    void deleteVehicle(Integer id, Integer userId);

    /**
     * 更新车辆信息
     */
    void updateVehicle(Vehicle vehicle, Integer userId);

    /**
     * 根据ID查询车辆
     */
    Vehicle getVehicleById(Integer id);

    /**
     * 查询所有车辆
     */
    List<Vehicle> getAllVehicles();

    /**
     * 分页查询车辆（支持按车型筛选）
     */
    IPage<Vehicle> getVehiclePage(Long current, Long size, Integer category);

    /**
     * 按车牌号搜索车辆
     */
    List<Vehicle> searchVehicles(String keyword);

    /**
     * 查询空闲车辆（按载重和容积筛选，按容量最接近排序推荐）
     * 体积 > 10 立方时强制只推荐中货/大货（category >= 2）
     */
    List<Vehicle> getAvailableVehicles(BigDecimal requiredWeight, BigDecimal requiredVolume, BigDecimal orderVolume);

    /**
     * 绑定司机到车辆
     */
    void bindDriver(Integer vehicleId, Integer driverId, Integer userId);

    /**
     * 解绑司机
     */
    void unbindDriver(Integer vehicleId, Integer userId);

    /**
     * 查询所有空闲司机（role=3且已录入驾驶证）
     */
    List<User> getAvailableDrivers();

    /**
     * 查询所有已录入驾驶证信息的司机
     */
    List<User> getAllDrivers();

    /**
     * 查询可用司机+车辆捆绑列表（仅返回有绑定车辆且车辆空闲的司机）
     * 按载重和容积筛选，按容量最接近排序推荐
     */
    List<DriverVehicleDTO> getAvailableDriverVehicles(BigDecimal requiredWeight, BigDecimal requiredVolume, BigDecimal orderVolume);
}
