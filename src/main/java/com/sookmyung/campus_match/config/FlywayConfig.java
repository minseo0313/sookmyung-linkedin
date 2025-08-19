package com.sookmyung.campus_match.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class FlywayConfig {

    private final Flyway flyway;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            log.info("Flyway 마이그레이션 전략 실행 중...");
            
            try {
                // 마이그레이션 실행
                log.info("마이그레이션을 실행합니다...");
                flyway.migrate();
                log.info("마이그레이션 완료");
                
            } catch (Exception e) {
                log.error("Flyway 마이그레이션 중 오류 발생: {}", e.getMessage());
                throw e;
            }
        };
    }
}
