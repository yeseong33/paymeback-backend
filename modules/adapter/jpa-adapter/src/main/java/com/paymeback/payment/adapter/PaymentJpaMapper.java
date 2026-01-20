package com.paymeback.payment.adapter;

import com.paymeback.gathering.entity.GatheringParticipantEntity;
import com.paymeback.payment.domain.Payment;
import com.paymeback.payment.entity.PaymentEntity;

public class PaymentJpaMapper {

    private PaymentJpaMapper() {
    }

    public static Payment toDomain(PaymentEntity entity) {
        if (entity == null) return null;
        return new Payment(
            entity.getId(),
            entity.getParticipant().getId(),
            entity.getAmount(),
            entity.getStatus(),
            entity.getExternalTransactionId(),
            entity.getCompletedAt(),
            entity.getFailureReason(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static PaymentEntity toEntity(Payment domain, GatheringParticipantEntity participant) {
        if (domain == null) return null;
        return PaymentEntity.builder()
            .id(domain.id())
            .participant(participant)
            .amount(domain.amount())
            .status(domain.status())
            .externalTransactionId(domain.externalTransactionId())
            .completedAt(domain.completedAt())
            .failureReason(domain.failureReason())
            .build();
    }
}
