package com.saas.subscription.security.jwt;

import com.saas.subscription.entity.UsersTable;
import com.saas.subscription.login.logindto.UserLoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.Base64;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.PropertyNamingStrategies;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTests {

    private static final byte[] SECRET_BYTES = "0123456789abcdef0123456789abcdef".getBytes();

    @Test
    void generatesSignedAccessTokenWithExpectedClaims() {
        JwtProperties properties = new JwtProperties(
                Base64.getEncoder().encodeToString(SECRET_BYTES),
                "https://api.jeevanandham.subscription",
                Duration.ofHours(1)
        );
        JwtService jwtService = new JwtService(
                new BCryptPasswordEncoder(),
                new JwtConfiguration().jwtEncoder(properties),
                properties
        );
        UsersTable user = UsersTable.builder()
                .userID(42L)
                .userEmail("user@example.com")
                .firstName("Jeeva")
                .build();

        JwtToken generated = jwtService.generateJWTToken(user);
        Jwt decoded = decoder().decode(generated.accessToken());

        assertThat(generated.tokenType()).isEqualTo("Bearer");
        assertThat(generated.expiresIn()).isEqualTo(3600L);
        assertThat(decoded.getSubject()).isEqualTo("42");
        assertThat(decoded.getIssuer().toString()).isEqualTo("https://api.jeevanandham.subscription");
        assertThat(decoded.getClaimAsString("email")).isEqualTo("user@example.com");
        assertThat(decoded.getClaimAsString("given_name")).isEqualTo("Jeeva");
        assertThat(decoded.getId()).isNotBlank();
        assertThat(decoded.getExpiresAt()).isAfter(decoded.getIssuedAt());
    }

    @Test
    void serializesLoginResponseAsJson() {
        UserLoginResponse response = new UserLoginResponse(
                "Login successful",
                "signed.jwt.token",
                "Bearer",
                3600L
        );

        String json = JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build()
                .writeValueAsString(response);

        assertThat(json).isEqualTo(
                "{\"message\":\"Login successful\",\"access_token\":\"signed.jwt.token\"," +
                        "\"token_type\":\"Bearer\",\"expires_in\":3600}"
        );
    }

    private JwtDecoder decoder() {
        SecretKey secretKey = new SecretKeySpec(SECRET_BYTES, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
