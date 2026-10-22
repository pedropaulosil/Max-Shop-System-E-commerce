package com.maxshop.api.controller;

import com.maxshop.api.dto.order.CheckoutRequest;
import com.maxshop.api.dto.order.OrderResponse;
import com.maxshop.api.entity.User;
import com.maxshop.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CheckoutRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(user, request));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> findMyOrders(
            @AuthenticationPrincipal User user, Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.findByCustomer(user, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(user, id));
    }
}
