package com.app.memberservice.services;

import com.app.memberservice.dto.LoginRequest;
import com.app.memberservice.dto.RegisterRequest;

public interface UserService {
    void register(RegisterRequest req);

    String login(LoginRequest req);
}
