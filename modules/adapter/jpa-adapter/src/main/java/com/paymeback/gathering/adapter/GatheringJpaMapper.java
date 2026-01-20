package com.paymeback.gathering.adapter;

import com.paymeback.gathering.domain.Gathering;
import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.gathering.entity.GatheringParticipantEntity;
import com.paymeback.user.entity.UserEntity;

import java.util.List;

public final class GatheringJpaMapper {

    private GatheringJpaMapper() {
    }

    public static Gathering toDomain(GatheringEntity entity) {
        if (entity == null) {
            return null;
        }

        List<Long> participantIds = entity.getParticipants().stream()
            .map(GatheringParticipantEntity::getId)
            .toList();

        return new Gathering(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getOwner().getId(),
            entity.getQrCode(),
            entity.getQrExpiresAt(),
            entity.getStatus(),
            entity.getTotalAmount(),
            entity.getScheduledAt(),
            participantIds,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static GatheringEntity toEntity(Gathering domain, UserEntity owner) {
        if (domain == null) {
            return null;
        }

        return GatheringEntity.builder()
            .title(domain.title())
            .description(domain.description())
            .owner(owner)
            .qrCode(domain.qrCode())
            .qrExpiresAt(domain.qrExpiresAt())
            .status(domain.status())
            .totalAmount(domain.totalAmount())
            .scheduledAt(domain.scheduledAt())
            .build();
    }
}
