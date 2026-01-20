package com.paymeback.user.domain;

public record EncodedPassword(String value) implements Password {

    public EncodedPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("암호화된 비밀번호가 비어있습니다.");
        }
    }

    public boolean sameAs(RawPassword other) {
        return this.value.equals(other.value());
    }
}