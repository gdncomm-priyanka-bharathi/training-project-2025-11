package com.app.cartservice.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private String category;
    private Double price;
}
