package com.paymeback.identity.domain;

import com.paymeback.identity.exception.InvalidOtpException;
import com.paymeback.identity.exception.OtpExpiredException;
import com.paymeback.identity.port.OtpSender;
import com.paymeback.identity.port.OtpStore;
import java.security.SecureRandom;
import java.time.Duration;

public class OtpVerification {

    private final OtpStore otpStore;
    private final OtpSender otpSender;
    private final int otpLength;
    private final int expirationMinutes;

    private static final SecureRandom random = new SecureRandom();

    public OtpVerification(OtpStore otpStore, OtpSender otpSender, int otpLength, int expirationMinutes) {
        this.otpStore = otpStore;
        this.otpSender = otpSender;
        this.otpLength = otpLength;
        this.expirationMinutes = expirationMinutes;
    }

    public void generateAndSend(String email) {
        String otpCode = generateOtpCode();
        otpStore.save(email, otpCode, Duration.ofMinutes(expirationMinutes));
        otpSender.send(email, otpCode);
    }

    public void verify(String email, String otpCode) {
        String storedOtp = otpStore.get(email)
            .orElseThrow(OtpExpiredException::new);

        if (!storedOtp.equals(otpCode)) {
            throw new InvalidOtpException();
        }
    }

    public void delete(String email) {
        otpStore.delete(email);
    }

    private String generateOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
