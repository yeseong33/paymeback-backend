package com.paymeback.user.domain;


import com.paymeback.user.exception.PasswordMismatchException;

public record RawPassword(String value) implements Password {

    public RawPassword {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
    }

    public void matchOrThrow(RawPassword confirm) {
        if (!value.equals(confirm.value())) {
            throw new PasswordMismatchException();
        }
    }
}