package com.app.cartservice.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String userName;
    private String phoneNumber;
}
