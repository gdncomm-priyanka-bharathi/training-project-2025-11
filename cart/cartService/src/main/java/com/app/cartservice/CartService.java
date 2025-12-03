package com.app.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.app.cartservice.client")
@SpringBootApplication
public class CartService {
    public static void main(String[] args) {
        SpringApplication.run(CartService.class, args);
    }
}