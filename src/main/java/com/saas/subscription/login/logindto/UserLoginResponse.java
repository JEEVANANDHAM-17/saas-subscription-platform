package com.saas.subscription.login.logindto;

public record UserLoginResponse(
        String message,
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
