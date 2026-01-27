package com.paymeback.payment.adapter;

import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.payment.domain.Settlement;
import com.paymeback.payment.entity.SettlementEntity;
import com.paymeback.user.entity.UserEntity;

public final class SettlementJpaMapper {

    private SettlementJpaMapper() {
    }

    public static Settlement toDomain(SettlementEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Settlement(
            entity.getId(),
            entity.getGatheringId(),
            entity.getFromUserId(),
            entity.getToUserId(),
            entity.getAmount(),
            entity.getStatus(),
            entity.getSettledAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static SettlementEntity toEntity(Settlement domain, GatheringEntity gathering,
                                            UserEntity fromUser, UserEntity toUser) {
        if (domain == null) {
            return null;
        }

        return SettlementEntity.builder()
            .gathering(gathering)
            .fromUser(fromUser)
            .toUser(toUser)
            .amount(domain.amount())
            .status(domain.status())
            .settledAt(domain.settledAt())
            .build();
    }
}
