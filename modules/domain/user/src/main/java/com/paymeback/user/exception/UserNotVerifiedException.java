package com.paymeback.user.exception;

public class UserNotVerifiedException extends UserException {

    public UserNotVerifiedException() {
        super("이메일 인증이 완료되지 않았습니다.");
    }
}