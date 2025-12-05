package com.app.cartservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", url = "http://localhost:8083/api/cart")
public interface CartClient {

    @DeleteMapping("/user/{userId}")
    void deleteCart(@PathVariable("userId") String userId);

}
