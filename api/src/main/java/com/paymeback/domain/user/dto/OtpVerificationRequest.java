package com.paymeback.domain.user.dto;


public record OtpVerificationRequest(

    String email,

    String otpCode
) {
}