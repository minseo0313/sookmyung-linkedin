package com.sookmyung.campus_match.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    private final Environment environment;

    public OpenApiConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public OpenAPI openAPI() {
        String[] activeProfiles = environment.getActiveProfiles();
        String currentProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        
        return new OpenAPI()
                .info(apiInfo())
                .servers(getServers(currentProfile))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private List<Server> getServers(String profile) {
        // 상대 경로를 사용하여 현재 접속한 호스트와 포트를 자동으로 따라감
        Server currentServer = new Server()
                .url("/")
                .description("현재 서버 (" + profile + " 프로필, 포트: " + serverPort + ")");
        
        // 프로필별 서버 정보 추가
        switch (profile) {
            case "local":
                return List.of(
                    currentServer,
                    new Server().url("http://localhost:8080").description("로컬 개발 서버 (포트: 8080)"),
                    new Server().url("http://localhost:8081").description("개발 서버 (포트: 8081)")
                );
            case "dev":
                return List.of(
                    currentServer,
                    new Server().url("http://localhost:8081").description("개발 서버 (포트: 8081)"),
                    new Server().url("http://localhost:8080").description("로컬 개발 서버 (포트: 8080)")
                );
            case "prod":
                return List.of(
                    currentServer,
                    new Server().url("https://api.campus-match.com").description("운영 서버")
                );
            default:
                return List.of(
                    currentServer,
                    new Server().url("http://localhost:8080").description("로컬 개발 서버 (포트: 8080)"),
                    new Server().url("http://localhost:8081").description("개발 서버 (포트: 8081)")
                );
        }
    }

    private Info apiInfo() {
        return new Info()
                .title("Campus Match API")
                .description("숙명여자대학교 캠퍼스 매칭 플랫폼 API 문서")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Campus Match Team")
                        .email("dev@campus-match.com")
                        .url("https://campus-match.com")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }
}
