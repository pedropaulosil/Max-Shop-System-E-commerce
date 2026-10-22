package com.maxshop.api.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CartItemRequest(
        @NotNull UUID productId,
        @NotNull @Min(1) Integer quantity
) {
}
