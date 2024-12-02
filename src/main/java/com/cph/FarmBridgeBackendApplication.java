package com.cph;

import com.cph.entity.PostBid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import sun.net.www.content.image.png;

import java.util.Date;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.cph.mapper")
public class FarmBridgeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmBridgeBackendApplication.class, args);
    }

}
