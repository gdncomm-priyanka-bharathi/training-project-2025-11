package com.app.memberservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", url = "http://localhost:8083/cart")
public interface CartClient {

    @DeleteMapping("/{userId}")
    void deleteCart(@PathVariable("userId") String userId);

}
