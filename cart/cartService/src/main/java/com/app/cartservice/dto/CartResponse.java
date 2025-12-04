package com.app.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private String id;
    private String customerId;
    private List<CartItemResponse> items;
    private double totalPrice;
}
