package com.paymeback.domain.user.dto;

import com.paymeback.user.domain.User;
import com.paymeback.user.domain.UserRole;

public record UserResponse(
    Long id,
    String email,
    String name,
    UserRole role,
    boolean verified,
    Long createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
            user.id(),
            user.email(),
            user.name(),
            user.role(),
            user.verified(),
            user.createdAt() != null ? user.createdAt().toEpochMilli() : null
        );
    }
}