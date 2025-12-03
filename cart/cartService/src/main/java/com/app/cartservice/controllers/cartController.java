package com.app.cartservice.controllers;

import com.app.cartservice.dto.AddToCartRequest;
import com.app.cartservice.dto.CartResponse;
import com.app.cartservice.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class cartController {

    private final CartService cartService;

    public cartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<String> addToCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AddToCartRequest request) {

        String customerId = jwt.getSubject();

        cartService.addToCart(customerId, request);

        return ResponseEntity.ok("Product added to cart");
    }



    @GetMapping("/viewCart")
    public ResponseEntity<CartResponse> viewCart(
            @AuthenticationPrincipal Jwt jwt) {

        String customerId = jwt.getSubject();

        return ResponseEntity.ok(cartService.viewCart(customerId));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> removeFromCart(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId) {

        String customerId = jwt.getSubject();

        cartService.removeFromCart(customerId, productId);

        return ResponseEntity.ok("Product removed successfully");
    }
}
