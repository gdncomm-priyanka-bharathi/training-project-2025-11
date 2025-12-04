package com.app.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;
    private String userName;
    private String phoneNumber;
}
