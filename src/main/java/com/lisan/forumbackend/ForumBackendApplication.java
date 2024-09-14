package com.lisan.forumbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lisan.forumbackend.mapper")
@EnableScheduling  // 启用定时任务功能
public class ForumBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumBackendApplication.class, args);
    }

}
