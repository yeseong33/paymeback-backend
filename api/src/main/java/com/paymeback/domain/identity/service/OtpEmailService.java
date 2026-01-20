package com.paymeback.domain.identity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.otp.expiration-minutes:5}")
    private int otpExpirationMinutes;

    @Async
    public void sendOtpEmail(String email, String otpCode) {
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
            // 비동기이므로 예외를 던지지 않고 로그만 남김
        }
    }
}
