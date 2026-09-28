package com.saas.subscription.security;

import com.saas.subscription.security.jwt.JwtAuthenticationEntryPoint;
import com.saas.subscription.security.jwt.JwtAuthenticationFilter;
import com.saas.subscription.security.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class SecurityConfig {


    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, JsonMapper jsonMapper)
            throws Exception
    {
        // Built here rather than as @Component beans so the servlet container does not also register the filter
        // on its own, which would run it twice per request.
        JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint(jsonMapper);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, authenticationEntryPoint);

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/login", "/signup").permitAll()
                        .requestMatchers("/", "/error", "/actuator/health").permitAll()
                        .anyRequest().authenticated())
                // Every other request needs "Authorization: Bearer <token>" from /login.
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
