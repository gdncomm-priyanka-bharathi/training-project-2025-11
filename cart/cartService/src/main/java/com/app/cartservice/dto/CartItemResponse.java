package com.app.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponse {
    private String productId;
    private String name;
    private String category;
    private Double price;
    private int quantity;
}
