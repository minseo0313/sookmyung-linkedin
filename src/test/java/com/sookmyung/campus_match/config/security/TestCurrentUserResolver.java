package com.sookmyung.campus_match.config.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestCurrentUserResolver implements CurrentUserResolver {
    @Override
    public Long currentUserId() {
        return 999L;
    }
}

