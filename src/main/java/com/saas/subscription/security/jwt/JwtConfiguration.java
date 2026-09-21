package com.saas.subscription.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
class JwtConfiguration {

    private static final int MINIMUM_SECRET_BYTES = 32;

    @Bean
    JwtEncoder jwtEncoder(JwtProperties properties)
    {
        return NimbusJwtEncoder.withSecretKey(secretKey(properties))
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(JwtProperties properties)
    {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey(properties))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        // Besides the signature, reject tokens that are expired or were issued by someone else.
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.issuer()));
        return jwtDecoder;
    }

    private static SecretKey secretKey(JwtProperties properties)
    {
        byte[] secretBytes;
        try
        {
            secretBytes = Base64.getDecoder().decode(properties.secret());
        }
        catch (IllegalArgumentException exception)
        {
            throw new IllegalStateException("app.jwt.secret must be valid Base64", exception);
        }

        if (secretBytes.length < MINIMUM_SECRET_BYTES)
        {
            throw new IllegalStateException("app.jwt.secret must contain at least 32 bytes");
        }

        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }
}
