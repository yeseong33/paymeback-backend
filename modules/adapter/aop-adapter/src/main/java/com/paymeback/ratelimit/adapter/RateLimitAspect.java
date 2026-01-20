package com.paymeback.ratelimit.adapter;

import com.paymeback.ratelimit.RateLimit;
import com.paymeback.ratelimit.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimiter rateLimiter;

    private static final String RATE_LIMIT_PREFIX = "rate:";

    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String clientIp = getClientIp();
        String key = buildKey(rateLimit.key(), clientIp, joinPoint);

        rateLimiter.checkLimit(key, rateLimit.maxAttempts(), rateLimit.windowSeconds());

        return joinPoint.proceed();
    }

    private String buildKey(String key, String clientIp, ProceedingJoinPoint joinPoint) {
        String baseKey = key.isEmpty() ? joinPoint.getSignature().getName() : key;
        return RATE_LIMIT_PREFIX + baseKey + ":" + clientIp;
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }

        HttpServletRequest request = attributes.getRequest();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
