package com.saas.subscription.security.jwt;

import com.saas.subscription.entity.UsersTable;
import com.saas.subscription.login.loginrepository.LoginRepository;
import com.saas.subscription.login.logindto.UserLoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.PropertyNamingStrategies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTests {

    private static final byte[] SECRET_BYTES = "0123456789abcdef0123456789abcdef".getBytes();
    private static final String ISSUER = "https://api.jeevanandham.subscription";

    private final LoginRepository loginRepository = mock(LoginRepository.class);
    private final JwtProperties properties = properties(SECRET_BYTES, ISSUER);
    private final JwtService jwtService = jwtService(properties);
    private final UsersTable user = UsersTable.builder()
            .userID(42L)
            .userEmail("user@example.com")
            .firstName("Jeeva")
            .build();

    @Test
    void generatesSignedAccessTokenWithExpectedClaims()
    {
        JwtToken generated = jwtService.generateJWTToken(user);
        Jwt decoded = decoder().decode(generated.accessToken());

        assertThat(generated.tokenType()).isEqualTo("Bearer");
        assertThat(generated.expiresIn()).isEqualTo(3600L);
        assertThat(decoded.getSubject()).isEqualTo("42");
        assertThat(decoded.getIssuer().toString()).isEqualTo(ISSUER);
        assertThat(decoded.getExpiresAt()).isAfter(decoded.getIssuedAt());
    }

    @Test
    void keepsThePayloadMinimalByCarryingOnlyTheUserId()
    {
        Jwt decoded = decoder().decode(jwtService.generateJWTToken(user).accessToken());

        assertThat(decoded.getClaims()).containsOnlyKeys("iss", "sub", "iat", "exp");
    }

    @Test
    void validatesTokenAndReturnsTheUserItWasIssuedFor()
    {
        when(loginRepository.findById(42L)).thenReturn(Optional.of(user));

        UsersTable validated = jwtService.validateJWTToken(jwtService.generateJWTToken(user).accessToken());

        assertThat(validated).isEqualTo(user);
    }

    @Test
    void rejectsATamperedOrMalformedToken()
    {
        assertThatThrownBy(() -> jwtService.validateJWTToken("not-a-valid-token"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void rejectsATokenSignedWithADifferentSecret()
    {
        JwtService otherSecret = jwtService(properties("fedcba9876543210fedcba9876543210".getBytes(), ISSUER));
        String token = otherSecret.generateJWTToken(user).accessToken();

        assertThatThrownBy(() -> jwtService.validateJWTToken(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void rejectsATokenFromAnotherIssuer()
    {
        JwtService otherIssuer = jwtService(properties(SECRET_BYTES, "https://someone-else.example"));
        String token = otherIssuer.generateJWTToken(user).accessToken();

        assertThatThrownBy(() -> jwtService.validateJWTToken(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void rejectsAnExpiredToken()
    {
        Instant twoHoursAgo = Instant.now().minus(Duration.ofHours(2));
        String token = encode(JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject("42")
                .issuedAt(twoHoursAgo)
                .expiresAt(twoHoursAgo.plus(Duration.ofHours(1)))
                .build());

        assertThatThrownBy(() -> jwtService.validateJWTToken(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void rejectsATokenWhoseSubjectIsNotAUserId()
    {
        Instant now = Instant.now();
        String token = encode(JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject("not-a-user-id")
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(1)))
                .build());

        assertThatThrownBy(() -> jwtService.validateJWTToken(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void rejectsATokenForAUserThatNoLongerExists()
    {
        when(loginRepository.findById(42L)).thenReturn(Optional.empty());
        String token = jwtService.generateJWTToken(user).accessToken();

        assertThatThrownBy(() -> jwtService.validateJWTToken(token))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void serializesLoginResponseAsJson()
    {
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

    private static JwtProperties properties(byte[] secret, String issuer)
    {
        return new JwtProperties(Base64.getEncoder().encodeToString(secret), issuer, Duration.ofHours(1));
    }

    private JwtService jwtService(JwtProperties properties)
    {
        JwtConfiguration jwtConfiguration = new JwtConfiguration();
        return new JwtService(
                new BCryptPasswordEncoder(),
                jwtConfiguration.jwtEncoder(properties),
                jwtConfiguration.jwtDecoder(properties),
                properties,
                loginRepository
        );
    }

    /** Signs arbitrary claims with the application secret, to build tokens generateJWTToken would never issue. */
    private String encode(JwtClaimsSet claims)
    {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return new JwtConfiguration().jwtEncoder(properties)
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    /** Signature-only decoder, so tests can inspect claims without the application's validation rules. */
    private JwtDecoder decoder()
    {
        SecretKey secretKey = new SecretKeySpec(SECRET_BYTES, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
