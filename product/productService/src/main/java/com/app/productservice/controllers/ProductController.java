package com.app.productservice.controllers;

import com.app.productservice.dto.ProductPageResponse;
import com.app.productservice.dto.ProductRequest;
import com.app.productservice.dto.ProductResponse;
import com.app.productservice.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService service) {
        this.productService = service;
    }


    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> addProduct(
            @RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.createProduct(request);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<ProductResponse> updateProductByID(
            @PathVariable String id,
            @RequestBody ProductRequest request){
        ProductResponse productResponse = productService.updateProduct(id, request);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/productDetail/{id}")
    public ResponseEntity<ProductResponse> getProductDetailById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }


    @GetMapping("/search")
    public ResponseEntity<ProductPageResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ProductPageResponse ProductResponse = productService.searchProducts(keyword, page, size);
        return new ResponseEntity<>(ProductResponse, HttpStatus.OK);
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<ProductResponse>> listAll() {
        return ResponseEntity.ok(productService.listAll());
    }









}
