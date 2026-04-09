package com.medical.smart_medical_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 智慧医疗平台主应用
 */
@SpringBootApplication
@MapperScan("com.medical.smart_medical_server.mapper")  // 扫描 Mapper 接口
@EnableCaching  // 启用 Spring Cache
public class SmartMedicalServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMedicalServerApplication.class, args);
    }
}