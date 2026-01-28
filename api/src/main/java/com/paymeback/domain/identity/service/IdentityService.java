package com.paymeback.domain.identity.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.user.service.UserService;
import com.paymeback.identity.domain.OtpVerification;
import com.paymeback.identity.port.OtpSender;
import com.paymeback.identity.port.OtpStore;
import com.paymeback.ratelimit.RateLimit;
import com.paymeback.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IdentityService {

    private final OtpVerification otpVerification;
    private final UserService userService;

    public IdentityService(OtpStore otpStore, OtpSender otpSender, UserService userService,
                           @Value("${app.otp.length:6}") int otpLength,
                           @Value("${app.otp.expiration-minutes:5}") int expirationMinutes) {
        this.otpVerification = new OtpVerification(otpStore, otpSender, otpLength, expirationMinutes);
        this.userService = userService;
    }

    public void verifyOtp(String email, String otpCode) {
        otpVerification.verify(email, otpCode);
        userService.verifyUser(email);
        otpVerification.delete(email);
    }

    @RateLimit(key = "resendOtp", maxAttempts = 5, windowSeconds = 3600)
    public void resendOtp(String email) {
        User user = userService.findByEmail(email);

        if (user.verified()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 인증된 사용자입니다.");
        }

        otpVerification.generateAndSend(email);
    }
}
