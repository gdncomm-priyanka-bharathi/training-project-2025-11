package com.app.memberservice.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserResponse{

    private Long id;
    private String email;
    private String userName;
    private String phoneNumber;

    }
