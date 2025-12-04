package com.app.cartservice.listener;

import com.app.cartservice.events.ProductUpdatedEvent;
import com.app.cartservice.services.impl.CartServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;

public class ProductUpdatedListener {

    private final CartServiceImpl cartService;

    public ProductUpdatedListener(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    @KafkaListener(
            topics = "product-updated-topic",
            groupId = "cart-service-group",
            containerFactory = "productUpdatedKafkaListenerContainerFactory")
    public void handleProductUpdated(ProductUpdatedEvent event) {
        cartService.applyProductUpdate(event);
    }
}
