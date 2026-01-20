package com.paymeback.payment.adapter;

import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.payment.domain.PaymentRequest;
import com.paymeback.payment.entity.PaymentRequestEntity;

public class PaymentRequestJpaMapper {

    private PaymentRequestJpaMapper() {
    }

    public static PaymentRequest toDomain(PaymentRequestEntity entity) {
        if (entity == null) return null;
        return new PaymentRequest(
            entity.getId(),
            entity.getGathering().getId(),
            entity.getTotalAmount(),
            entity.getAmountPerPerson(),
            entity.getParticipantCount(),
            entity.getExpiresAt(),
            entity.getCreatedAt()
        );
    }

    public static PaymentRequestEntity toEntity(PaymentRequest domain, GatheringEntity gathering) {
        if (domain == null) return null;
        return PaymentRequestEntity.builder()
            .id(domain.id())
            .gathering(gathering)
            .totalAmount(domain.totalAmount())
            .amountPerPerson(domain.amountPerPerson())
            .participantCount(domain.participantCount())
            .expiresAt(domain.expiresAt())
            .build();
    }
}
