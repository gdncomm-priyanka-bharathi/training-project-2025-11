package com.app.cartservice.services;

import com.app.cartservice.dto.AddToCartRequest;
import com.app.cartservice.dto.CartResponse;

public interface CartService {
    void addToCart(String customerId, AddToCartRequest request);

    CartResponse viewCart(String customerId);

    void removeFromCart(String customerId, String productId);
}
