package com.warehouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 仓储管理系统主启动类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 * @date 2025-01-11
 */
@SpringBootApplication
@MapperScan("com.warehouse.mapper")
public class WarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("仓储管理系统启动成功！");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}

