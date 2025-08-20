package com.sookmyung.campus_match.config;

import com.sookmyung.campus_match.config.security.jwt.JwtTokenProvider;
import com.sookmyung.campus_match.config.security.dev.DevPrincipalResolver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.http.HttpServletRequest;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(mockUserDetailsService());
    }

    @Bean
    public UserDetailsService mockUserDetailsService() {
        return username -> org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("password")
                .authorities("USER")
                .build();
    }

    @Bean
    @Primary
    public DevPrincipalResolver devPrincipalResolver() {
        return new DevPrincipalResolver() {
            @Override
            public Long currentUserId(HttpServletRequest request) {
                return 999L;
            }
        };
    }
}
