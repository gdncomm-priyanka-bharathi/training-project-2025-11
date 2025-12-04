package com.app.cartservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdatedEvent {
    private String productId;
    private String name;
    private String description;
    private String category;
    private double price;
}
