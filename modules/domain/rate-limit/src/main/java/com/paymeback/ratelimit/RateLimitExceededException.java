package com.paymeback.ratelimit;

public class RateLimitExceededException extends RuntimeException {

    private final String key;
    private final int maxAttempts;

    public RateLimitExceededException(String key, int maxAttempts) {
        super(String.format("Rate limit exceeded for key: %s (max: %d)", key, maxAttempts));
        this.key = key;
        this.maxAttempts = maxAttempts;
    }

    public String getKey() {
        return key;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
}
