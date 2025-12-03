package com.app.memberservice.controllers;

import com.app.memberservice.dto.LoginRequest;
import com.app.memberservice.dto.LoginResponse;
import com.app.memberservice.dto.RegisterRequest;
import com.app.memberservice.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name ="User",description = "APIs for user login register")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        userService.register(req);
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        String token = userService.login(req);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
