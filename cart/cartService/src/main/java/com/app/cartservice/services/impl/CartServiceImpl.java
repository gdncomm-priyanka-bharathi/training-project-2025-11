package com.app.cartservice.services.impl;

import com.app.cartservice.cache.ProductCache;
import com.app.cartservice.client.ProductRestClient;
import com.app.cartservice.dto.AddToCartRequest;
import com.app.cartservice.dto.CartItemResponse;
import com.app.cartservice.dto.CartResponse;
import com.app.cartservice.dto.ProductResponse;
import com.app.cartservice.entity.CartItem;
import com.app.cartservice.entity.ShoppingCart;
import com.app.cartservice.repositories.CartRepository;
import com.app.cartservice.repositories.ProductCacheRepository;
import com.app.cartservice.services.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductCacheRepository productCacheRepository;
    private final ProductRestClient productRestClient;

    public CartServiceImpl(CartRepository cartRepository, ProductCacheRepository productCacheRepository, ProductRestClient productRestClient) {
        this.cartRepository = cartRepository;
        this.productCacheRepository = productCacheRepository;
        this.productRestClient = productRestClient;
    }

    @Override
    public void addToCart(String customerId, AddToCartRequest request) {

        ProductCache cache = productCacheRepository.findById(request.getProductId())
                .orElse(null);

        if (cache == null) {
            // Call Product Service and fetch details
            ProductResponse productResponse = productRestClient.getProductById(request.getProductId());

            // Save to cache for future use
            cache = new ProductCache();
            cache.setProductId(productResponse.getId());
            cache.setName(productResponse.getName());
            cache.setCategory(productResponse.getCategory());
            cache.setPrice(productResponse.getPrice());

            productCacheRepository.save(cache);
        }

        ShoppingCart cart = cartRepository.findById(customerId)
                .orElse(new ShoppingCart(customerId));

        Optional<CartItem> existing = cart.getItems()
                .stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
        } else {
            cart.getItems().add(new CartItem(request.getProductId(), request.getQuantity()));
        }

        cartRepository.save(cart);
    }



    @Override
    public CartResponse viewCart(String customerId) {
        ShoppingCart cart = cartRepository.findById(customerId)
                .orElse(new ShoppingCart(customerId));

        List<CartItemResponse> responseList = new ArrayList<>();

        for (CartItem item : cart.getItems()) {

            ProductCache cache = productCacheRepository.findById(item.getProductId())
                    .orElse(null);

            if (cache == null) continue;

            responseList.add(new CartItemResponse(
                    cache.getProductId(),
                    cache.getName(),
                    cache.getCategory(),
                    cache.getPrice(),
                    item.getQuantity()
            ));
        }

        return new CartResponse(responseList);
    }

    @Override
    public void removeFromCart(String customerId, String productId) {

        ShoppingCart cart = cartRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(i -> i.getProductId().equals(productId));

        cartRepository.save(cart);
    }
}
