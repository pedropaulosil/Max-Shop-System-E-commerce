package com.maxshop.api.dto.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String name,
        String email,
        String role
) {
}
