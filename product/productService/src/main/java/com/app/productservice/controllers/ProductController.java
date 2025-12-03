package com.app.productservice.controllers;

import com.app.productservice.dto.ProductResponse;
import com.app.productservice.dto.ProductUpdateRequest;
import com.app.productservice.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService service) {
        this.productService = service;
    }

    @GetMapping("/productDetail/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }


    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ProductResponse> ProductResponse = productService.searchProducts(keyword, page, size);
        return new ResponseEntity<>(ProductResponse, HttpStatus.OK);
    }

    @PutMapping("/productDetail/update{id}")
    public ResponseEntity<ProductResponse> updateProductByID(
            @PathVariable String id,
            @RequestBody ProductUpdateRequest request){
        ProductResponse productResponse = productService.updateProductDetail(id, request);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> addProduct(
            @RequestBody ProductUpdateRequest request) {

        ProductResponse productResponse = productService.addProduct(request);

        return ResponseEntity.ok(productResponse);
    }





}
