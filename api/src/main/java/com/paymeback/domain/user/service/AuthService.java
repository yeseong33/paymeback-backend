package com.paymeback.domain.user.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.identity.domain.OtpVerification;
import com.paymeback.identity.port.OtpSender;
import com.paymeback.identity.port.OtpStore;
import com.paymeback.ratelimit.RateLimit;
import com.paymeback.user.domain.User;
import com.paymeback.domain.user.dto.SignInRequest;
import com.paymeback.domain.user.dto.SignUpRequest;
import com.paymeback.domain.user.dto.UserResponse;
import com.paymeback.domain.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final OtpVerification otpVerification;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, OtpStore otpStore, OtpSender otpSender,
                       JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
                       @Value("${app.otp.length:6}") int otpLength,
                       @Value("${app.otp.expiration-minutes:5}") int expirationMinutes) {
        this.userService = userService;
        this.otpVerification = new OtpVerification(otpStore, otpSender, otpLength, expirationMinutes);
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @RateLimit(key = "signup", maxAttempts = 5, windowSeconds = 3600)
    public UserResponse signUp(SignUpRequest request) {
        UserResponse userResponse = userService.createUser(request);

        // OTP 발송
        otpVerification.generateAndSend(request.email());

        return userResponse;
    }

    public String signIn(SignInRequest request) {
        User user = userService.findByEmail(request.email());

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.password())) {
            log.error("Invalid email or password : {}, {}", request.password(),  user.password());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 이메일 인증 여부 확인
        if (!user.verified()) {
            throw new BusinessException(ErrorCode.USER_NOT_VERIFIED);
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.email());
        log.info("사용자가 로그인했습니다. email: {}", user.email());

        return token;
    }
}
