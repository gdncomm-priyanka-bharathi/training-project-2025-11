package com.app.memberservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponse(){

    private Long id;
    private String email;
    private String fullName;

}
