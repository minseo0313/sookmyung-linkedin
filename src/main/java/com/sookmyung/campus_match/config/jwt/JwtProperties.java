package com.sookmyung.campus_match.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret = "your-secret-key-here-make-it-long-and-secure-for-production";
    private Long accessTokenExpiration = 3600L; // 1 hour in seconds
    private Long refreshTokenExpiration = 86400L; // 24 hours in seconds
}
