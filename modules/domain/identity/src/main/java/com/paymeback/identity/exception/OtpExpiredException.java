package com.paymeback.identity.exception;

public class OtpExpiredException extends OtpException {

    public OtpExpiredException() {
        super("OTP가 존재하지 않거나 만료되었습니다.");
    }
}
