package com.paymeback.identity.exception;

public class InvalidOtpException extends OtpException {

    public InvalidOtpException() {
        super("유효하지 않은 OTP입니다.");
    }
}
