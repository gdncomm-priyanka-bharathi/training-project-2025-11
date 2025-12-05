package com.app.productservice.services.impl;

import com.app.productservice.dto.ProductPageResponse;
import com.app.productservice.dto.ProductRequest;
import com.app.productservice.dto.ProductResponse;
import com.app.productservice.entity.Product;
import com.app.productservice.events.ProductUpdatedEvent;
import com.app.productservice.exceptions.ResourceNotFoundException;
import com.app.productservice.repositories.ProductRepository;
import com.app.productservice.services.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductUpdatedEvent> kafkaTemplate;
    private final String topicName = "product-updated-topic";

    public ProductServiceImpl(ProductRepository repo, KafkaTemplate<String, ProductUpdatedEvent> kafkaTemplate) {
        this.productRepository = repo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        // Create a new Product entity
        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        // Save product to DB
        Product saved = productRepository.save(product);

        return convertToDTO(saved);
    }


    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PRODUCT_NOT_FOUND"));
        BeanUtils.copyProperties(request, product);
        Product updated = productRepository.save(product);

        // Publish Kafka event
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        event.setProductId(updated.getId());
        event.setName(updated.getName());
        event.setDescription(updated.getDescription());
        event.setCategory(updated.getCategory());
        event.setPrice(updated.getPrice());

        kafkaTemplate.send(topicName, updated.getId(), event);

        return convertToDTO(updated);
    }

    @Override
    public ProductResponse getProductDetail(String id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("PRODUCT_NOT_FOUND"));

            // Convert entity → response DTO
            return convertToDTO(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public ProductPageResponse searchProducts(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.searchProducts(keyword, pageRequest);

        // Convert Product → ProductResponse using BeanUtils
        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Prepare paginated response
        ProductPageResponse response = new ProductPageResponse();
        response.setItems(content);
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setPage(page);
        response.setSize(size);

        return response;
    }

    private ProductResponse convertToDTO(Product product) {
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        return productResponse;
    }


}
