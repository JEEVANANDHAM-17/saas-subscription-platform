package com.saas.subscription.security.jwt;

import com.saas.subscription.entity.UsersTable;
import com.saas.subscription.login.loginrepository.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token";

    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;
    private final LoginRepository loginRepository;

    public String encodePassword(String password)
    {
        return passwordEncoder.encode(password);
    }

    public boolean matchesPassword(String password, String encodedPassword)
    {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public JwtToken generateJWTToken(UsersTable user)
    {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.accessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(Long.toString(user.getUserID()))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));

        return new JwtToken(
                jwt.getTokenValue(),
                "Bearer",
                jwtProperties.accessTokenTtl().toSeconds()
        );
    }

    public UsersTable validateJWTToken(String jwtToken)
    {
        Jwt jwt;
        try
        {
            jwt = jwtDecoder.decode(jwtToken);
        }
        catch (JwtException exception)
        {
            throw new BadCredentialsException(INVALID_TOKEN_MESSAGE, exception);
        }

        long userId;
        try
        {
            userId = Long.parseLong(jwt.getSubject());
        }
        catch (NumberFormatException exception)
        {
            throw new BadCredentialsException(INVALID_TOKEN_MESSAGE, exception);
        }

        return loginRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(INVALID_TOKEN_MESSAGE));
    }
}
