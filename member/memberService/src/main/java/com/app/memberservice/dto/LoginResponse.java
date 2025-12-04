package com.app.memberservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private UserResponse member;

    }
