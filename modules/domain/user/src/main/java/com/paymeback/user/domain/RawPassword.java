package com.paymeback.user.domain;

public record RawPassword(String value) implements Password {
    public RawPassword {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
    }

    public boolean match(RawPassword other) {
        return value.equals(other.value());
    }
}