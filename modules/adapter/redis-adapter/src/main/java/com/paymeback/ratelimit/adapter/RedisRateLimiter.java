package com.paymeback.ratelimit.adapter;

import com.paymeback.ratelimit.RateLimitExceededException;
import com.paymeback.ratelimit.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRateLimiter implements RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void checkLimit(String key, int maxAttempts, int windowSeconds) {
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;

        if (count >= maxAttempts) {
            log.warn("Rate limit exceeded. key: {}, count: {}", key, count);
            throw new RateLimitExceededException(key, maxAttempts);
        }

        Long newCount = redisTemplate.opsForValue().increment(key);
        if (newCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
    }
}
