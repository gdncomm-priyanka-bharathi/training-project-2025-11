package com.app.productservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String category;
    private Double price;
}
