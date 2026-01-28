package com.paymeback.user.exception;

public class UserNotFoundException extends UserException {

    public UserNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }
}