package com.app.cartservice.services.impl;

import com.app.cartservice.client.ProductRestClient;
import com.app.cartservice.client.UserClient;
import com.app.cartservice.dto.AddToCartRequest;
import com.app.cartservice.dto.CartResponse;
import com.app.cartservice.dto.ProductResponse;
import com.app.cartservice.dto.UserResponse;
import com.app.cartservice.entity.CartItem;
import com.app.cartservice.entity.ShoppingCart;
import com.app.cartservice.events.ProductUpdatedEvent;
import com.app.cartservice.exceptions.EmptyCartException;
import com.app.cartservice.exceptions.ItemNotFoundException;
import com.app.cartservice.exceptions.UserNotFoundException;
import com.app.cartservice.exceptions.UserNotLoggedInException;
import com.app.cartservice.repositories.CartRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRestClient productRestClient;

    @Mock
    private UserClient userClient;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(redis.opsForValue()).thenReturn(valueOperations);
    }

    // ==================== addToCart Tests ====================

    @Test
    void testAddToCart_NewCart_Success() {
        String customerId = "customer123";
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId("prod1");
        request.setQuantity(2);

        ProductResponse product = new ProductResponse();
        product.setId("prod1");
        product.setName("Laptop");
        product.setCategory("Electronics");
        product.setPrice(999.99);

        ShoppingCart savedCart = new ShoppingCart();
        savedCart.setId("cart123");
        savedCart.setCustomerId(customerId);
        CartItem item = new CartItem();
        item.setProductId("prod1");
        item.setName("Laptop");
        item.setCategory("Electronics");
        item.setPrice(999.99);
        item.setQuantity(2);
        savedCart.getItems().add(item);
        savedCart.setTotalPrice(1999.98);

        when(redis.hasKey("LOGIN:" + customerId)).thenReturn(true);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
        when(productRestClient.getProductDetail("prod1")).thenReturn(product);
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(savedCart);

        CartResponse response = cartService.addToCart(customerId, request);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(1, response.getItems().size());
        assertEquals("Laptop", response.getItems().get(0).getName());
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testAddToCart_ExistingCart_AddNewItem() {
        String customerId = "customer123";
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId("prod2");
        request.setQuantity(1);

        ShoppingCart existingCart = new ShoppingCart();
        existingCart.setId("cart123");
        existingCart.setCustomerId(customerId);
        CartItem existingItem = new CartItem();
        existingItem.setProductId("prod1");
        existingItem.setName("Laptop");
        existingItem.setPrice(999.99);
        existingItem.setQuantity(1);
        existingCart.getItems().add(existingItem);

        ProductResponse product = new ProductResponse();
        product.setId("prod2");
        product.setName("Mouse");
        product.setCategory("Accessories");
        product.setPrice(29.99);

        when(redis.hasKey("LOGIN:" + customerId)).thenReturn(true);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingCart));
        when(productRestClient.getProductDetail("prod2")).thenReturn(product);
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addToCart(customerId, request);

        assertNotNull(response);
        assertEquals(2, response.getItems().size());
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testAddToCart_ExistingItem_UpdateQuantity() {
        String customerId = "customer123";
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId("prod1");
        request.setQuantity(2);

        ShoppingCart existingCart = new ShoppingCart();
        existingCart.setId("cart123");
        existingCart.setCustomerId(customerId);
        CartItem existingItem = new CartItem();
        existingItem.setProductId("prod1");
        existingItem.setName("Laptop");
        existingItem.setPrice(999.99);
        existingItem.setQuantity(1);
        existingCart.getItems().add(existingItem);

        ProductResponse product = new ProductResponse();
        product.setId("prod1");
        product.setName("Laptop");
        product.setCategory("Electronics");
        product.setPrice(999.99);

        when(redis.hasKey("LOGIN:" + customerId)).thenReturn(true);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingCart));
        when(productRestClient.getProductDetail("prod1")).thenReturn(product);
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addToCart(customerId, request);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(3, response.getItems().get(0).getQuantity()); // 1 + 2 = 3
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testAddToCart_UserNotLoggedIn() {
        String customerId = "customer123";
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId("prod1");
        request.setQuantity(1);

        when(redis.hasKey("LOGIN:" + customerId)).thenReturn(false);

        assertThrows(UserNotLoggedInException.class,
                () -> cartService.addToCart(customerId, request));

        verify(cartRepository, never()).save(any(ShoppingCart.class));
    }

    @Test
    void testAddToCart_ProductNotFound() {
        String customerId = "customer123";
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId("prod1");
        request.setQuantity(1);

        when(redis.hasKey("LOGIN:" + customerId)).thenReturn(true);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
        when(productRestClient.getProductDetail("prod1")).thenReturn(null);

        assertThrows(ItemNotFoundException.class,
                () -> cartService.addToCart(customerId, request));
    }

    // ==================== updateQuantity Tests ====================

    @Test
    void testUpdateQuantity_Success() {
        String customerId = "customer123";
        String productId = "prod1";

        ShoppingCart cart = new ShoppingCart();
        cart.setId("cart123");
        cart.setCustomerId(customerId);
        CartItem item = new CartItem();
        item.setProductId(productId);
        item.setName("Laptop");
        item.setPrice(999.99);
        item.setQuantity(1);
        cart.getItems().add(item);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.updateQuantity(customerId, productId, 5);

        assertNotNull(response);
        assertEquals(5, response.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testUpdateQuantity_CartNotFound() {
        String customerId = "customer123";

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(EmptyCartException.class,
                () -> cartService.updateQuantity(customerId, "prod1", 5));
    }

    @Test
    void testUpdateQuantity_ItemNotFound() {
        String customerId = "customer123";

        ShoppingCart cart = new ShoppingCart();
        cart.setId("cart123");
        cart.setCustomerId(customerId);
        CartItem item = new CartItem();
        item.setProductId("prod1");
        cart.getItems().add(item);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        assertThrows(ItemNotFoundException.class,
                () -> cartService.updateQuantity(customerId, "prod999", 5));
    }

    // ==================== removeItem Tests ====================

    @Test
    void testRemoveItem_Success() {
        String customerId = "customer123";
        String productId = "prod1";

        ShoppingCart cart = new ShoppingCart();
        cart.setId("cart123");
        cart.setCustomerId(customerId);
        CartItem item1 = new CartItem();
        item1.setProductId(productId);
        item1.setPrice(999.99);
        item1.setQuantity(1);
        CartItem item2 = new CartItem();
        item2.setProductId("prod2");
        item2.setPrice(29.99);
        item2.setQuantity(2);
        cart.getItems().add(item1);
        cart.getItems().add(item2);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.removeItem(customerId, productId);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals("prod2", response.getItems().get(0).getProductId());
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testRemoveItem_CartNotFound() {
        String customerId = "customer123";

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(EmptyCartException.class,
                () -> cartService.removeItem(customerId, "prod1"));
    }

    // ==================== viewCart Tests ====================

    @Test
    void testViewCart_Success() {
        String customerId = "customer123";

        UserResponse user = new UserResponse();
        user.setId(123L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId("cart123");
        cart.setCustomerId(customerId);
        CartItem item = new CartItem();
        item.setProductId("prod1");
        item.setName("Laptop");
        item.setPrice(999.99);
        item.setQuantity(1);
        cart.getItems().add(item);
        cart.setTotalPrice(999.99);

        when(userClient.getUser(customerId)).thenReturn(user);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.viewCart(customerId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(1, response.getItems().size());
        assertEquals(999.99, response.getTotalPrice());
    }

    @Test
    void testViewCart_NullCustomerId() {
        assertThrows(UserNotLoggedInException.class,
                () -> cartService.viewCart(null));
    }

    @Test
    void testViewCart_BlankCustomerId() {
        assertThrows(UserNotLoggedInException.class,
                () -> cartService.viewCart("   "));
    }

    @Test
    void testViewCart_UserNotFound() {
        String customerId = "customer123";

        when(userClient.getUser(customerId)).thenThrow(FeignException.NotFound.class);

        assertThrows(UserNotFoundException.class,
                () -> cartService.viewCart(customerId));
    }

    @Test
    void testViewCart_CartNotFound() {
        String customerId = "customer123";

        UserResponse user = new UserResponse();
        user.setId(123L);

        when(userClient.getUser(customerId)).thenReturn(user);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThrows(EmptyCartException.class,
                () -> cartService.viewCart(customerId));
    }

    @Test
    void testViewCart_EmptyCart() {
        String customerId = "customer123";

        UserResponse user = new UserResponse();
        user.setId(123L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId("cart123");
        cart.setCustomerId(customerId);
        cart.setItems(new ArrayList<>());

        when(userClient.getUser(customerId)).thenReturn(user);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        assertThrows(EmptyCartException.class,
                () -> cartService.viewCart(customerId));
    }

    // ==================== applyProductUpdate Tests ====================

    @Test
    void testApplyProductUpdate_Success() {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        event.setProductId("prod1");
        event.setName("Updated Laptop");
        event.setDescription("New Description");
        event.setCategory("Electronics");
        event.setPrice(899.99);

        ShoppingCart cart = new ShoppingCart();
        cart.setId("cart123");
        cart.setCustomerId("customer123");
        CartItem item = new CartItem();
        item.setProductId("prod1");
        item.setName("Laptop");
        item.setPrice(999.99);
        item.setQuantity(1);
        cart.getItems().add(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.applyProductUpdate(event);

        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void testApplyProductUpdate_NoMatchingProduct() {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        event.setProductId("prod999");
        event.setName("Some Product");

        ShoppingCart cart = new ShoppingCart();
        cart.setId("cart123");
        CartItem item = new CartItem();
        item.setProductId("prod1");
        cart.getItems().add(item);

        when(cartRepository.findAll()).thenReturn(List.of(cart));

        cartService.applyProductUpdate(event);

        verify(cartRepository, never()).save(any(ShoppingCart.class));
    }

    // ==================== deleteCartForUser Tests ====================

    @Test
    void testDeleteCartForUser_Success() {
        String userId = "customer123";

        cartService.deleteCartForUser(userId);

        verify(cartRepository, times(1)).deleteByCustomerId(userId);
    }
}

