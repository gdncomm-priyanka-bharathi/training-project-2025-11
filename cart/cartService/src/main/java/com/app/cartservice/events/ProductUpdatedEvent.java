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
    private String category;
    private Double price;
//    private boolean active;
}
