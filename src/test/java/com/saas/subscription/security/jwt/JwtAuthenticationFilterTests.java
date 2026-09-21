package com.saas.subscription.security.jwt;

import com.saas.subscription.entity.UsersTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTests {

    private final JwtService jwtService = mock(JwtService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            jwtService,
            new JwtAuthenticationEntryPoint(JsonMapper.builder().build())
    );
    private final UsersTable user = UsersTable.builder()
            .userID(42L)
            .userEmail("user@example.com")
            .build();

    @AfterEach
    void clearSecurityContext()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void letsRequestsWithoutAnAuthorizationHeaderThroughUnauthenticated() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/plan");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(chain.getRequest()).isSameAs(request);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtService);
    }

    @Test
    void ignoresAuthorizationHeadersThatAreNotBearerTokens() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/plan");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(chain.getRequest()).isSameAs(request);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtService);
    }

    @Test
    void authenticatesTheRequestAsTheUserTheTokenWasIssuedFor() throws Exception
    {
        when(jwtService.validateJWTToken("valid.jwt.token")).thenReturn(user);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/plan");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(chain.getRequest()).isSameAs(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isSameAs(user);
    }

    @Test
    void acceptsTheBearerSchemeRegardlessOfCase() throws Exception
    {
        when(jwtService.validateJWTToken("valid.jwt.token")).thenReturn(user);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/plan");
        request.addHeader(HttpHeaders.AUTHORIZATION, "bearer valid.jwt.token");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isSameAs(user);
    }

    @Test
    void rejectsAnInvalidTokenWithoutReachingTheEndpoint() throws Exception
    {
        when(jwtService.validateJWTToken("bad.jwt.token"))
                .thenThrow(new BadCredentialsException("Invalid or expired token"));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/plan");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(chain.getRequest()).isNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getHeader(HttpHeaders.WWW_AUTHENTICATE)).isEqualTo("Bearer");
        assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString())
                .isEqualTo("{\"code\":401,\"message\":\"Invalid or expired token\"}");
    }
}
