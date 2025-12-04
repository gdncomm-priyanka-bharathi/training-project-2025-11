package com.app.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductListResponse {

    private List<ProductResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

}
