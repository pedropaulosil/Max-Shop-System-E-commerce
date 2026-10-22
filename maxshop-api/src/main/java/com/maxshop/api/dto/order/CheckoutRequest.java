package com.maxshop.api.dto.order;

import com.maxshop.api.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotBlank String shippingAddress,
        @NotNull PaymentMethod paymentMethod,
        String couponCode
) {
}
