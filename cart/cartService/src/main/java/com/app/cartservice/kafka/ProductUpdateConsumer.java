package com.app.cartservice.kafka;

import com.app.cartservice.cache.ProductCache;
import com.app.cartservice.events.ProductUpdatedEvent;
import com.app.cartservice.repositories.ProductCacheRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductUpdateConsumer {

    private final ProductCacheRepository cacheRepository;

    public ProductUpdateConsumer(ProductCacheRepository repo) {
        this.cacheRepository = repo;
    }

    @KafkaListener(topics = "product-updated", groupId = "cart-service")
    public void handleProductUpdate(ProductUpdatedEvent event) {

        ProductCache cache = new ProductCache();
        BeanUtils.copyProperties(event, cache);

        cacheRepository.save(cache);
    }
}
