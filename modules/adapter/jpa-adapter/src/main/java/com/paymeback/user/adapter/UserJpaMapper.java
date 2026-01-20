package com.paymeback.user.adapter;

import com.paymeback.user.domain.User;
import com.paymeback.user.entity.UserEntity;

public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getPassword(),
            entity.getName(),
            entity.getRole(),
            entity.isVerified(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
            .id(domain.id())
            .email(domain.email())
            .password(domain.password())
            .name(domain.name())
            .role(domain.role())
            .verified(domain.verified())
            .build();
    }
}