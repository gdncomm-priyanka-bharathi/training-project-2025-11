package com.app.cartservice.services;

import com.app.cartservice.dto.AddToCartRequest;
import com.app.cartservice.dto.CartResponse;
import jakarta.validation.Valid;

public interface CartService {
    CartResponse addToCart(String customerId, @Valid AddToCartRequest request);

    CartResponse updateQuantity(String customerId, String productId, int quantity);

    CartResponse viewCart(String customerId);

    CartResponse removeItem(String customerId, String productId);
}
