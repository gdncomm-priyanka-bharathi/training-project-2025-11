package com.app.gateway.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenStore {
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private final StringRedisTemplate redisTemplate;

    public TokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String jti, long ttlSeconds) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + jti,
                "1",
                Duration.ofSeconds(ttlSeconds)
        );
    }

    public boolean isBlacklisted(String jti) {
        Boolean exist = redisTemplate.hasKey(BLACKLIST_PREFIX + jti);
        return exist != null && exist;
    }

}
