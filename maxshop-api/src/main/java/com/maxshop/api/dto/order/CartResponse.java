package com.maxshop.api.dto.order;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record CartResponse(
        List<Item> items,
        BigDecimal subtotal
) {
    @Builder
    public record Item(
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal lineTotal
    ) {
    }
}
