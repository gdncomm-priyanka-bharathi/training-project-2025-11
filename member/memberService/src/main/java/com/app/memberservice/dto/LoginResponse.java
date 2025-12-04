package com.app.memberservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginResponse {
    private String token;
    private UserResponse member;

    }
