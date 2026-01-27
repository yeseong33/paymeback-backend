package com.paymeback.domain.identity.service;

import com.paymeback.cache.KeyValueStore;
import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.user.service.UserService;
import com.paymeback.ratelimit.RateLimit;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityService {

    private final OtpService otpService;
    private final UserService userService;
    private final KeyValueStore keyValueStore;

    private static final String OTP_PREFIX = "otp:";

    public void verifyOtp(String email, String otpCode) {

        String key = OTP_PREFIX + email;

        String storedOtp = keyValueStore.get(key)
            .orElseThrow(() -> {
                log.warn("OTP 없음 또는 만료. email: {}", email);
                return new IllegalStateException("OTP not found or expired");
            });

        otpService.verifyOtp(storedOtp, otpCode);

        userService.verifyUser(email);

        otpService.deleteOtp(email);
    }

    @RateLimit(key = "resendOtp", maxAttempts = 5, windowSeconds = 3600)
    public void resendOtp(String email) {
        // 사용자 존재 여부 확인
        User user = userService.findByEmail(email);

        // 이미 인증된 사용자인지 확인
        if (user.verified()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 인증된 사용자입니다.");
        }

        otpService.generateAndSendOtp(email);
    }

}
