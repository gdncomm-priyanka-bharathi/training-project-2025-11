package com.app.cartservice.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("product_cache")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCache {
    @Id
    private String productId;

    private String name;
    private String category;
    private Double price;
}
