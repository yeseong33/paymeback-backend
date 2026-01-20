package com.paymeback.ratelimit;

/**
 * Rate limiting을 위한 포트 인터페이스.
 * 구현체는 Redis, In-Memory 등 다양한 저장소를 사용할 수 있음.
 */
public interface RateLimiter {

    /**
     * Rate limit을 확인하고 초과 시 예외를 던짐.
     *
     * @param key           rate limit key
     * @param maxAttempts   최대 시도 횟수
     * @param windowSeconds 윈도우 시간(초)
     * @throws RateLimitExceededException rate limit 초과 시
     */
    void checkLimit(String key, int maxAttempts, int windowSeconds);
}
