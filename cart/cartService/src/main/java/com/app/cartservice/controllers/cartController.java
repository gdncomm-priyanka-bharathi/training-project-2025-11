package com.app.cartservice.controllers;

import com.app.cartservice.dto.AddToCartRequest;
import com.app.cartservice.dto.CartResponse;
import com.app.cartservice.services.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class cartController {

    private final CartService cartService;

    public cartController(CartService cartService) {
        this.cartService = cartService;
    }

    private String getCustomerIdFromHeader(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            throw new RuntimeException("UNAUTHENTICATED");
        }
        return userIdHeader;
    }


    @PostMapping("/addToCart")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddToCartRequest request) {
        String customerId = getCustomerIdFromHeader(userId);
        return ResponseEntity.ok(cartService.addToCart(customerId, request));
    }

//    @PutMapping("/items/{productId}")
//    public ResponseEntity<CartResponse> updateQuantity(
//            @RequestHeader("X-User-Id") String userId,
//            @PathVariable String productId,
//            @RequestParam int quantity) {
//        String customerId = getCustomerIdFromHeader(userId);
//        return ResponseEntity.ok(cartService.updateQuantity(customerId, productId, quantity));
//    }


    @GetMapping("/viewCart")
    public ResponseEntity<CartResponse> viewCart(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        String customerId = getCustomerIdFromHeader(userId);
        return ResponseEntity.ok(cartService.viewCart(customerId));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String productId) {
        String customerId = getCustomerIdFromHeader(userId);
        return ResponseEntity.ok(cartService.removeItem(customerId, productId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteCart(@PathVariable String userId) {
        cartService.deleteCartForUser(userId);
        return ResponseEntity.ok("Cart deleted for user: " + userId);
    }


}
