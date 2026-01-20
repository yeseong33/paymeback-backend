package com.paymeback.user.domain;

import java.time.Instant;

public record User(
    Long id,
    String email,
    String password,  // 지금은 그대로 유지 (나중에 passwordHash로 바꾸는 거 추천)
    String name,
    UserRole role,
    boolean verified,
    Instant createdAt,
    Instant updatedAt
) {

    public static User createUser(String email, String encodedPassword, String name) {
        return new User(null, email, encodedPassword, name, UserRole.USER, false, null, null);
    }

    public User verifyEmail() {
        return new User(
            id,
            email,
            password,
            name,
            role,
            true,
            createdAt,
            updatedAt
        );
    }
}

