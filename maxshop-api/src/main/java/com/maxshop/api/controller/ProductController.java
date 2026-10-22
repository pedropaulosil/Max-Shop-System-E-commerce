package com.maxshop.api.controller;

import com.maxshop.api.dto.product.ProductRequest;
import com.maxshop.api.dto.product.ProductResponse;
import com.maxshop.api.entity.User;
import com.maxshop.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/products")
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable
    ) {
        return ResponseEntity.ok(productService.search(q, categoryId, minPrice, maxPrice, pageable));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("/api/sellers/products")
    public ResponseEntity<ProductResponse> create(
            @AuthenticationPrincipal User seller,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(seller, request));
    }

    @PutMapping("/api/sellers/products/{id}")
    public ResponseEntity<ProductResponse> update(
            @AuthenticationPrincipal User seller,
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.update(seller, id, request));
    }

    @DeleteMapping("/api/sellers/products/{id}")
    public ResponseEntity<Void> deactivate(@AuthenticationPrincipal User seller, @PathVariable UUID id) {
        productService.deactivate(seller, id);
        return ResponseEntity.noContent().build();
    }
}
