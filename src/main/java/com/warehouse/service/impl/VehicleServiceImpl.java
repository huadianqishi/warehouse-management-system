package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.entity.User;
import com.warehouse.entity.Vehicle;
import com.warehouse.entity.dto.DriverVehicleDTO;
import com.warehouse.exception.BusinessException;
import com.warehouse.mapper.LogisticsOrderMapper;
import com.warehouse.mapper.UserMapper;
import com.warehouse.mapper.VehicleMapper;
import com.warehouse.service.OperationLogService;
import com.warehouse.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆服务实现类
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addVehicle(Vehicle vehicle, Integer userId) {
        if (vehicle.getPlateNumber() == null || vehicle.getPlateNumber().trim().isEmpty()) {
            throw new BusinessException("车牌号不能为空");
        }
        QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
        wrapper.eq("plate_number", vehicle.getPlateNumber());
        if (vehicleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("车牌号已存在");
        }
        if (vehicle.getCategory() == null) {
            throw new BusinessException("车型不能为空");
        }
        if (vehicle.getMaxWeight() == null || vehicle.getMaxWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("最大载重必须大于0");
        }
        if (vehicle.getMaxVolume() == null || vehicle.getMaxVolume().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("最大容积必须大于0");
        }
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(0);
        }
        int result = vehicleMapper.insert(vehicle);
        if (result <= 0) {
            throw new BusinessException("添加车辆失败");
        }
        operationLogService.logOperation(userId, 19, "成功", "vehicle", vehicle.getId(),
                "添加车辆：" + vehicle.getPlateNumber());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVehicle(Integer id, Integer userId) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException("车辆不存在");
        }
        QueryWrapper<com.warehouse.entity.LogisticsOrder> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("vehicle_id", id);
        orderWrapper.in("status", 0, 1, 2); // 只检查未完成的订单（待指派、已指派、运输中）
        // 如果车辆本身是空闲状态（0），则允许删除；只有在使用中（1）且有关联活跃订单时才阻止
        if (vehicle.getStatus() != 0 && logisticsOrderMapper.selectCount(orderWrapper) > 0) {
            throw new BusinessException("该车辆仍存在订单，无法删除");
        }
        // 解除外键引用：清空所有关联订单的 vehicle_id 和 driver_id
        logisticsOrderMapper.clearVehicleRelation(id);
        int result = vehicleMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除车辆失败");
        }
        operationLogService.logOperation(userId, 20, "成功", "vehicle", id,
                "删除车辆：" + vehicle.getPlateNumber());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVehicle(Vehicle vehicle, Integer userId) {
        Vehicle old = vehicleMapper.selectById(vehicle.getId());
        if (old == null) {
            throw new BusinessException("车辆不存在");
        }
        if (vehicle.getPlateNumber() != null && !vehicle.getPlateNumber().equals(old.getPlateNumber())) {
            QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
            wrapper.eq("plate_number", vehicle.getPlateNumber()).ne("id", vehicle.getId());
            if (vehicleMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("车牌号已存在");
            }
        }
        int result = vehicleMapper.updateById(vehicle);
        if (result <= 0) {
            throw new BusinessException("修改车辆失败");
        }
        operationLogService.logOperation(userId, 21, "成功", "vehicle", vehicle.getId(),
                "修改车辆信息");
    }

    @Override
    public Vehicle getVehicleById(Integer id) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException("车辆不存在");
        }
        return vehicle;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = vehicleMapper.selectList(null);
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCurrentDriverId() != null) {
                User driver = userMapper.selectById(vehicle.getCurrentDriverId());
                if (driver != null) {
                    vehicle.setDriverName(driver.getRealName());
                }
            }
        }
        return vehicles;
    }

    @Override
    public IPage<Vehicle> getVehiclePage(Long current, Long size, Integer category) {
        Page<Vehicle> page = new Page<>(current, size);
        QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
        if (category != null) {
            wrapper.eq("category", category);
        }
        IPage<Vehicle> result = vehicleMapper.selectPage(page, wrapper);
        for (Vehicle vehicle : result.getRecords()) {
            if (vehicle.getCurrentDriverId() != null) {
                User driver = userMapper.selectById(vehicle.getCurrentDriverId());
                if (driver != null) {
                    vehicle.setDriverName(driver.getRealName());
                }
            }
        }
        return result;
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword) {
        QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
        wrapper.like("plate_number", keyword);
        List<Vehicle> vehicles = vehicleMapper.selectList(wrapper);
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCurrentDriverId() != null) {
                User driver = userMapper.selectById(vehicle.getCurrentDriverId());
                if (driver != null) {
                    vehicle.setDriverName(driver.getRealName());
                }
            }
        }
        return vehicles;
    }

    @Override
    public List<Vehicle> getAvailableVehicles(BigDecimal requiredWeight, BigDecimal requiredVolume, BigDecimal orderVolume) {
        QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        if (requiredWeight != null) {
            wrapper.ge("max_weight", requiredWeight);
        }
        if (requiredVolume != null) {
            wrapper.ge("max_volume", requiredVolume);
        }
        // 体积 > 10 立方时强制只推荐中货/大货（category >= 2）
        if (orderVolume != null && orderVolume.compareTo(new BigDecimal("10")) > 0) {
            wrapper.ge("category", 2);
        }
        List<Vehicle> vehicles = vehicleMapper.selectList(wrapper);

        // 按"刚刚好够用"排序：容量超出量（weight+volume之和）越小越靠前
        return vehicles.stream()
                .sorted(Comparator.comparing(v -> {
                    BigDecimal excessWeight = v.getMaxWeight().subtract(requiredWeight != null ? requiredWeight : BigDecimal.ZERO);
                    BigDecimal excessVolume = v.getMaxVolume().subtract(requiredVolume != null ? requiredVolume : BigDecimal.ZERO);
                    return excessWeight.add(excessVolume);
                }))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindDriver(Integer vehicleId, Integer driverId, Integer userId) {
        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle == null) {
            throw new BusinessException("车辆不存在");
        }
        vehicle.setCurrentDriverId(driverId);
        vehicleMapper.updateById(vehicle);
        // 记录操作日志（操作类型ID=22：绑定司机）
        operationLogService.logOperation(userId, 22, "成功", "vehicle", vehicleId,
                "绑定司机：车辆ID=" + vehicleId + "，司机ID=" + driverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindDriver(Integer vehicleId, Integer userId) {
        Vehicle vehicle = vehicleMapper.selectById(vehicleId);
        if (vehicle == null) {
            throw new BusinessException("车辆不存在");
        }
        vehicle.setCurrentDriverId(null);
        vehicleMapper.updateById(vehicle);
        // 记录操作日志（操作类型ID=21：修改车辆）
        operationLogService.logOperation(userId, 21, "成功", "vehicle", vehicleId,
                "解除绑定司机：车辆ID=" + vehicleId);
    }

    @Override
    public List<User> getAvailableDrivers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role", 3)
                .isNotNull("driver_license")
                .ne("driver_license", "");
        return userMapper.selectList(wrapper);
    }

    @Override
    public List<User> getAllDrivers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role", 3);
        return userMapper.selectList(wrapper);
    }

    @Override
    public List<DriverVehicleDTO> getAvailableDriverVehicles(BigDecimal requiredWeight, BigDecimal requiredVolume, BigDecimal orderVolume) {
        List<Vehicle> vehicles = getAvailableVehicles(requiredWeight, requiredVolume, orderVolume);
        return vehicles.stream().filter(v -> v.getCurrentDriverId() != null).map(v -> {
            User driver = userMapper.selectById(v.getCurrentDriverId());
            DriverVehicleDTO dto = new DriverVehicleDTO();
            dto.setDriverId(v.getCurrentDriverId());
            dto.setDriverName(driver != null ? driver.getRealName() : null);
            dto.setDriverPhone(driver != null ? driver.getPhone() : null);
            dto.setDriverLicense(driver != null ? driver.getDriverLicense() : null);
            dto.setVehicleId(v.getId());
            dto.setPlateNumber(v.getPlateNumber());
            dto.setCategory(v.getCategory());
            dto.setCategoryText(getCategoryText(v.getCategory()));
            dto.setMaxWeight(v.getMaxWeight());
            dto.setMaxVolume(v.getMaxVolume());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    private String getCategoryText(Integer category) {
        if (category == null) return "未知";
        switch (category) {
            case 0: return "微面";
            case 1: return "小货";
            case 2: return "中货";
            case 3: return "大货";
            default: return "未知";
        }
    }
}
