package com.example.shoppingmall.config;

import com.example.shoppingmall.jwt.JwtTokenFilter;
import com.example.shoppingmall.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/users/login",
                                        "/users/register"
                                )
                                .anonymous()

                                .requestMatchers(
                                        "/users/current-user",
                                        "/users/fill-essential",
                                        "/users/update-avatar",
                                        "/users/business",
                                        "/users/business/{id}/accept",
                                        "/users/business/{id}/decline",
                                        "/item",
                                        "/item/{id}",
                                        "/item/{id}/proposal"
                                )
                                .authenticated()

                )
                // JWT를 사용하기 때문에 보안 관련 세션 해제
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenUtils),
                        AuthorizationFilter.class
                );

        return http.build();
    }
}
