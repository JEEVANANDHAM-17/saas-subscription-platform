package com.saas.subscription.security.jwt;

public record JwtToken(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
