package com.app.cartservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String productId;

    private String name;
    private String description;
    private String category;
    private Double price;

    private int quantity;
}
