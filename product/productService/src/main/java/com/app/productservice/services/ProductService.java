package com.app.productservice.services;

import com.app.productservice.dto.ProductResponse;
import com.app.productservice.dto.ProductUpdateRequest;

import java.util.List;

public interface ProductService {
    ProductResponse getProductById(String id);

    List<ProductResponse> searchProducts(String keyword, int page, int size);

    ProductResponse updateProductDetail(String id, ProductUpdateRequest request);

    ProductResponse addProduct(ProductUpdateRequest request);
}
