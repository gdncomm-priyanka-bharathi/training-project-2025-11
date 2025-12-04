package com.app.productservice.services;

import com.app.productservice.dto.ProductPageResponse;
import com.app.productservice.dto.ProductRequest;
import com.app.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(String id, ProductRequest request);
    ProductResponse getProductDetail(String id);
    ProductPageResponse searchProducts(String keyword, int page, int size);

    List<ProductResponse> listAll();
}
