package com.saas.subscription.security.jwt;

import com.saas.subscription.entity.UsersTable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Authenticates requests carrying an {@code Authorization: Bearer <jwt>} header.
 * Requests without one pass through unauthenticated, so the rules in SecurityConfig decide whether they are allowed.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String token = resolveBearerToken(request);
        if (token == null)
        {
            filterChain.doFilter(request, response);
            return;
        }

        UsersTable user;
        try
        {
            user = jwtService.validateJWTToken(token);
        }
        catch (AuthenticationException exception)
        {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, exception);
            return;
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(user, null, List.of()));
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }

    private static String resolveBearerToken(HttpServletRequest request)
    {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(header, BEARER_PREFIX))
        {
            return null;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
