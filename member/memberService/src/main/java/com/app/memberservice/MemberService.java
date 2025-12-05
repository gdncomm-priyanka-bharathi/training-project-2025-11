package com.app.memberservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.app.memberservice.client")
@SpringBootApplication
public class MemberService {
    public static void main(String[] args) {
        SpringApplication.run(MemberService.class, args);
    }
}
