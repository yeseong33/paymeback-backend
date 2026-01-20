package com.paymeback.domain.identity.domain;

import com.paymeback.cache.KeyValueStore;
import java.security.SecureRandom;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Otp implements Identity {

    private final KeyValueStore keyValueStore;
    private final JavaMailSender mailSender;

    @Value("${app.otp.expiration-minutes:5}")
    private int otpExpirationMinutes;

    @Value("${app.otp.length:6}")
    private int otpLength;

    private static final String OTP_PREFIX = "otp:";
    private static final SecureRandom random = new SecureRandom();

    public void generateAndSendOtp(String email) {
        String otpCode = generateOtpCode();

        // Redis에 OTP 저장 (만료시간 설정)
        String key = OTP_PREFIX + email;
        keyValueStore.set(key, otpCode, Duration.ofMinutes(otpExpirationMinutes));

        // 이메일 발송
        sendOtpEmail(email, otpCode);

        log.info("OTP가 생성되고 발송되었습니다. email: {}", email);
    }

    public boolean verifyOtp(String email, String otpCode) {
        String key = OTP_PREFIX + email;
        String storedOtp = keyValueStore.get(key).orElse(null);

        if (storedOtp == null) {
            log.warn("OTP가 존재하지 않거나 만료되었습니다. email: {}", email);
            return false;
        }

        boolean isValid = storedOtp.equals(otpCode);
        log.info("OTP 검증 결과: {} for email: {}", isValid, email);

        return isValid;
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

    private void sendOtpEmail(String email, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Pay Me Back 이메일 인증 코드");
            message.setText(String.format(
                "안녕하세요!\n\n" +
                    "Pay Me Back 서비스 이메일 인증을 위한 코드입니다.\n\n" +
                    "인증 코드: %s\n\n" +
                    "이 코드는 %d분 후에 만료됩니다.\n\n" +
                    "감사합니다.",
                otpCode, otpExpirationMinutes
            ));

            mailSender.send(message);
            log.info("OTP 이메일이 발송되었습니다. email: {}", email);

        } catch (Exception e) {
            log.error("OTP 이메일 발송에 실패했습니다. email: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
}
