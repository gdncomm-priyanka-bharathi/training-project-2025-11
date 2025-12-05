package com.app.cartservice.services.impl;

import com.app.cartservice.client.ProductRestClient;
import com.app.cartservice.client.UserClient;
import com.app.cartservice.dto.AddToCartRequest;
import com.app.cartservice.dto.CartItemResponse;
import com.app.cartservice.dto.CartResponse;
import com.app.cartservice.dto.ProductResponse;
import com.app.cartservice.entity.CartItem;
import com.app.cartservice.entity.ShoppingCart;
import com.app.cartservice.events.ProductUpdatedEvent;
import com.app.cartservice.exceptions.*;
import com.app.cartservice.repositories.CartRepository;
import com.app.cartservice.services.CartService;
import feign.FeignException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRestClient productRestClient;
    private final UserClient userClient;
    private final StringRedisTemplate redis;


    public CartServiceImpl(CartRepository cartRepository, ProductRestClient productRestClient, UserClient userClient, StringRedisTemplate redis) {
        this.cartRepository = cartRepository;
        this.productRestClient = productRestClient;
        this.userClient = userClient;
        this.redis = redis;
    }

    @Override
    public CartResponse addToCart(String customerId, AddToCartRequest request) {
        String sessionKey = "LOGIN:" + customerId;
        Boolean isLoggedIn = redis.hasKey(sessionKey);

        if (Boolean.FALSE.equals(isLoggedIn)) {
            throw new UserNotLoggedInException("USER_SESSION_EXPIRED_OR_LOGGED_OUT");
        }

        ShoppingCart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    ShoppingCart c = new ShoppingCart();
                    c.setCustomerId(customerId);
                    return c;
                });

        ProductResponse product = productRestClient.getProductDetail(request.getProductId());
        if (product == null) {
            throw new ItemNotFoundException("PRODUCT_NOT_FOUND");
        }

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setPrice(product.getPrice());
            item.setCategory(product.getCategory());
            item.setName(product.getName());

        } else {
            CartItem item = new CartItem();
            item.setProductId(request.getProductId());
            item.setQuantity(request.getQuantity());
            item.setCategory(product.getCategory());
            item.setPrice(product.getPrice());
            item.setName(product.getName());
            cart.getItems().add(item);
        }

        recalcTotal(cart);
        ShoppingCart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }



    @Override
    public CartResponse updateQuantity(String customerId, String productId, int quantity) {
        ShoppingCart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EmptyCartException("CART_NOT_FOUND"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("ITEM_NOT_FOUND"));

        item.setQuantity(quantity);
        recalcTotal(cart);
        ShoppingCart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    public CartResponse removeItem(String customerId, String productId) {
        ShoppingCart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EmptyCartException("CART_NOT_FOUND"));

        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        recalcTotal(cart);
        ShoppingCart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    @Override
    public CartResponse viewCart(String customerId) {

        if (customerId == null || customerId.isBlank()) {
            throw new UserNotLoggedInException("USER_IS_NOT_LOGGED_IN");
        }

        //Validate user existence from Member Service
        try {
            userClient.getUser(customerId);

        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("USER_DOES_NOT_EXIST");

        } catch (FeignException e) {
            //any other feign issue = remote service failed
            throw new RemoteServiceException("MEMBER_SERVICE_ERROR: " + e.getMessage());
        }

        //Check cart existence
        ShoppingCart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EmptyCartException("CART_DOES_NOT_EXISTS"));

        //Check if cart is empty
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("CART_IS_EMPTY");
        }

        return mapToResponse(cart);
    }



    public void applyProductUpdate(ProductUpdatedEvent event) {
        List<ShoppingCart> carts = cartRepository.findAll();
        boolean changed = false;

        for (ShoppingCart cart : carts) {
            for (CartItem item : cart.getItems()) {
                if (item.getProductId().equals(event.getProductId())) {
                    item.setName(event.getName());
                    item.setDescription(event.getDescription());
                    item.setCategory(event.getCategory());
                    item.setPrice(event.getPrice());
                    changed = true;
                }
            }
            if (changed) {
                recalcTotal(cart);
                cartRepository.save(cart);
                changed = false;
            }
        }
    }

    private void recalcTotal(ShoppingCart cart) {
        double total = cart.getItems().stream()
                .filter(i -> i.getPrice() != null)
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        int qty = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        cart.setTotalPrice(total);
    }

    @KafkaListener(topics = "user-deleted")
    public void deleteCartForUser(String userId) {
        cartRepository.deleteByCustomerId(userId);
    }


    private CartResponse mapToResponse(ShoppingCart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setCustomerId(cart.getCustomerId());
        response.setTotalPrice(cart.getTotalPrice());

        List<CartItemResponse> items = cart.getItems().stream().map(i -> {
            CartItemResponse r = new CartItemResponse();
            BeanUtils.copyProperties(i, r);
            return r;
        }).collect(Collectors.toList());

        response.setItems(items);
        return response;
    }


}
