package com.maxshop.api.dto.product;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record ProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal compareAtPrice,
        Integer stockQuantity,
        String categoryName,
        String sellerName,
        List<String> images
) {
}
