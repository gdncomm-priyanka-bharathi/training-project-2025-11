package com.app.productservice.services.impl;

import com.app.productservice.dto.ProductResponse;
import com.app.productservice.dto.ProductUpdateRequest;
import com.app.productservice.entity.Product;
import com.app.productservice.events.ProductUpdatedEvent;
import com.app.productservice.exceptions.ResourceNotFoundException;
import com.app.productservice.kafka.ProductUpdateProducer;
import com.app.productservice.repositories.ProductRepository;
import com.app.productservice.services.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductUpdateProducer productUpdateProducer;

    public ProductServiceImpl(ProductRepository repo, ProductUpdateProducer productUpdateProducer) {
        this.productRepository = repo;
        this.productUpdateProducer = productUpdateProducer;
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Convert entity → response DTO
        return convertToDTO(product);
    }


    @Override
    public List<ProductResponse> searchProducts(String keyword, int page, int size) {
        Page<Product> paged = productRepository.searchProducts(keyword, PageRequest.of(page, size));

        // Convert entity → response DTO in a List
        return paged.stream().map(this::convertToDTO).toList();
    }

    @Override
    public ProductResponse updateProductDetail(String id, ProductUpdateRequest request) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));

        // Copy fields from request → entity
        BeanUtils.copyProperties(request, existing);

        // Save updated product
        Product saved = productRepository.save(existing);

        //Publish update event to Kafka
        ProductUpdatedEvent event = new ProductUpdatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getCategory(),
                saved.getPrice()
        );

        productUpdateProducer.publish(event);   //Kafka publish

        // Convert entity → response DTO
        return convertToDTO(saved);
    }

    @Override
    public ProductResponse addProduct(ProductUpdateRequest request) {
        // Create a new Product entity
        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        // Save product to DB
        Product saved = productRepository.save(product);

        // Convert and return response
        return convertToDTO(saved);
    }

    private ProductResponse convertToDTO(Product product) {
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        return productResponse;
    }


}
