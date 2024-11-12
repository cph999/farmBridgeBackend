package com.cph;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.cph.mapper")
public class FarmBridgeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmBridgeBackendApplication.class, args);
    }

}
