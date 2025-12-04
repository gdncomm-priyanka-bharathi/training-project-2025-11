package com.app.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private String productId;
    private String name;
    private String description;
    private String category;
    private Double price;
    private int quantity;
}
