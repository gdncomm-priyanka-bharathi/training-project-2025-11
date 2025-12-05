package com.app.memberservice.services;

import com.app.memberservice.dto.LoginResponse;
import com.app.memberservice.dto.LoginRequest;
import com.app.memberservice.dto.RegisterRequest;
import com.app.memberservice.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);

    void logout(String userId);

    UserResponse getUserById(String id);
}
