package com.sookmyung.campus_match.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"!dev", "!test"})
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Swagger 허용 (스웨거 쓰면)
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()

                        // 비로그인 허용
                        .requestMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()

                        // 그 외는 인증 필요(지금은 JWT 없으니 사실상 막힘 → 곧 붙일 예정)
                        .anyRequest().authenticated()
                )
                .httpBasic(h -> h.disable())   // Basic 미사용
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                ));
        return http.build();
    }
}
