package com.app.memberservice.controllers;

import com.app.memberservice.dto.LoginRequest;
import com.app.memberservice.dto.LoginResponse;
import com.app.memberservice.dto.RegisterRequest;
import com.app.memberservice.dto.UserResponse;
import com.app.memberservice.services.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "APIs for user login register")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(
            @Parameter(
            name = "X-User-Id",
            required = true,
            in = ParameterIn.HEADER)
            @RequestHeader("X-User-Id") String userId) {
        // Actual token blacklisting is done in gateway filter.
        userService.logout(userId);
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }


}
