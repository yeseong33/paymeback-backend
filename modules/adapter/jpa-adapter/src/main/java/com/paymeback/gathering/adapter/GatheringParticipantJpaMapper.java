package com.paymeback.gathering.adapter;

import com.paymeback.gathering.domain.GatheringParticipant;
import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.gathering.entity.GatheringParticipantEntity;
import com.paymeback.user.entity.UserEntity;

public final class GatheringParticipantJpaMapper {

    private GatheringParticipantJpaMapper() {
    }

    public static GatheringParticipant toDomain(GatheringParticipantEntity entity) {
        if (entity == null) {
            return null;
        }

        return new GatheringParticipant(
            entity.getId(),
            entity.getGathering().getId(),
            entity.getUser().getId(),
            entity.getJoinedAt()
        );
    }

    public static GatheringParticipantEntity toEntity(GatheringParticipant domain,
            GatheringEntity gathering, UserEntity user) {
        if (domain == null) {
            return null;
        }

        return GatheringParticipantEntity.builder()
            .gathering(gathering)
            .user(user)
            .build();
    }
}
