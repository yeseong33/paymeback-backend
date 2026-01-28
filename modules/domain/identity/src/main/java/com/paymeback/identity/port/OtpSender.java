package com.paymeback.identity.port;

public interface OtpSender {

    void send(String email, String otpCode);
}
