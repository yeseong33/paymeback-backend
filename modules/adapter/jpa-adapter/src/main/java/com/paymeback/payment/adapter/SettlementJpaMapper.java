package com.paymeback.payment.adapter;

import com.paymeback.payment.domain.Settlement;
import com.paymeback.payment.entity.SettlementEntity;

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

    public static SettlementEntity toEntity(Settlement domain) {
        if (domain == null) {
            return null;
        }

        return SettlementEntity.builder()
            .gatheringId(domain.gatheringId())
            .fromUserId(domain.fromUserId())
            .toUserId(domain.toUserId())
            .amount(domain.amount())
            .status(domain.status())
            .settledAt(domain.settledAt())
            .build();
    }
}
