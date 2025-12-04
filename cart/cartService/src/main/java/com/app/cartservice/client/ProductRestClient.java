package com.app.cartservice.client;

import com.app.cartservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "http://localhost:8082/products"
)
public interface ProductRestClient {

    @GetMapping("/productDetail/{id}")
    ProductResponse getProductDetail(@PathVariable String id);
}
