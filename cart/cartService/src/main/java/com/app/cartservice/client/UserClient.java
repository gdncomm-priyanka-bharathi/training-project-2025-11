package com.app.cartservice.client;

import com.app.cartservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service", url = "http://localhost:8081/user")
public interface UserClient {
    @GetMapping("/{id}")
    UserResponse getUser(@PathVariable("id") String id);
}
