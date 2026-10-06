package com.warehouse.config;

import com.warehouse.entity.User;
import com.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 在应用启动时自动创建默认用户
 *
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        createSuperAdmin();
        createWarehouseKeeper();
        createAdmin();
        createDriver();
    }

    private void createSuperAdmin() {
        if (userService.getUserByUsername("superadmin") == null) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword("123456");
            superAdmin.setRealName("超级管理员");
            superAdmin.setGender(1);
            superAdmin.setPhone("10000000000");
            superAdmin.setIdCard("110101197001010001");
            superAdmin.setRole(0);
            userService.register(superAdmin, null);
            System.out.println("INFO: Default superadmin user created.");
        }
    }

    private void createWarehouseKeeper() {
        if (userService.getUserByUsername("keeper") == null) {
            User keeper = new User();
            keeper.setUsername("keeper");
            keeper.setPassword("123456");
            keeper.setRealName("仓管员");
            keeper.setGender(1);
            keeper.setPhone("10000000001");
            keeper.setIdCard("110101197001010002");
            keeper.setRole(1);
            userService.register(keeper, null);
            System.out.println("INFO: Default warehouse keeper user created.");
        }
    }

    private void createAdmin() {
        if (userService.getUserByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setRealName("普通用户");
            admin.setGender(1);
            admin.setPhone("10000000002");
            admin.setIdCard("110101197001010003");
            admin.setRole(2);
            userService.register(admin, null);
            System.out.println("INFO: Default normal user created.");
        }
    }

    private void createDriver() {
        if (userService.getUserByUsername("driver1") == null) {
            User driver = new User();
            driver.setUsername("driver1");
            driver.setPassword("123456");
            driver.setRealName("司机张三");
            driver.setGender(1);
            driver.setPhone("10000000003");
            driver.setIdCard("110101197001010004");
            driver.setRole(3);
            driver.setDriverLicense("A2-123456789");
            userService.register(driver, null);
            System.out.println("INFO: Default driver user created.");
        }
    }
}
