package com.app.productservice.kafka;

import com.app.productservice.events.ProductUpdatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductUpdateProducer {

    private final KafkaTemplate<String, ProductUpdatedEvent> kafkaTemplate;

    public ProductUpdateProducer(KafkaTemplate<String, ProductUpdatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ProductUpdatedEvent event) {
        kafkaTemplate.send("product-updated", event.getProductId(), event);
    }

}
