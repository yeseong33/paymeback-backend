package com.paymeback.identity.exception;

public abstract class OtpException extends RuntimeException {

    protected OtpException(String message) {
        super(message);
    }

    protected OtpException(String message, Throwable cause) {
        super(message, cause);
    }
}
