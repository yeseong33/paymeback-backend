package com.paymeback.domain.identity.service;

import com.paymeback.cache.KeyValueStore;
import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final KeyValueStore keyValueStore;
    private final OtpEmailService otpEmailService;

    @Value("${app.otp.expiration-minutes:5}")
    private int otpExpirationMinutes;

    @Value("${app.otp.length:6}")
    private int otpLength;

    private static final String OTP_PREFIX = "otp:";
    private static final SecureRandom random = new SecureRandom();

    public void generateAndSendOtp(String email) {
        String otpCode = generateOtpCode();

        // Redis에 OTP 저장 (만료시간 설정) - 동기
        String key = OTP_PREFIX + email;
        keyValueStore.set(key, otpCode, Duration.ofMinutes(otpExpirationMinutes));

        log.info("OTP가 생성되었습니다. email: {}", email);

        // 이메일 발송 - 비동기
        otpEmailService.sendOtpEmail(email, otpCode);
    }

    public void verifyOtp(String actual, String expected) {
        if (!actual.equals(expected)) {
            log.warn("OTP 검증 실패 (없음/만료/불일치). actual: {}, expected: {}", actual, expected);
            throw new BusinessException(ErrorCode.INVALID_OTP);
        }
    }

    public void deleteOtp(String email) {
        String key = OTP_PREFIX + email;
        keyValueStore.delete(key);
        log.info("OTP가 삭제되었습니다. email: {}", email);
    }

    private String generateOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
