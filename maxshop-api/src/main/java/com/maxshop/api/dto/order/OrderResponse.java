package com.maxshop.api.dto.order;

import com.maxshop.api.entity.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponse(
        UUID id,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal discount,
        BigDecimal total,
        String shippingAddress,
        Instant createdAt,
        List<Item> items
) {
    @Builder
    public record Item(String productName, Integer quantity, BigDecimal unitPrice) {
    }
}
