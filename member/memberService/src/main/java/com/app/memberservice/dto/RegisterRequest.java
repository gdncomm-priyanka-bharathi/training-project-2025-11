package com.app.memberservice.dto;

public record RegisterRequest(String userName,String email, String password,String phoneNumber) {
}
