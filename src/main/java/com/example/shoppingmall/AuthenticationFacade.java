package com.example.shoppingmall;

import com.example.shoppingmall.entity.CustomUserDetails;
import com.example.shoppingmall.service.JpaUserDetailsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFacade {
    private final JpaUserDetailsManager userDetailsManager;
    public CustomUserDetails getCurrentUserDetails() {
        String username
                = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return (CustomUserDetails) userDetailsManager.loadUserByUsername(username);
        } catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
