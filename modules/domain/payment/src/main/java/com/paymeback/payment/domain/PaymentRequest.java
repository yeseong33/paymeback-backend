package com.paymeback.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentRequest(
    Long id,
    Long gatheringId,
    BigDecimal totalAmount,
    BigDecimal amountPerPerson,
    int participantCount,
    Instant expiresAt,
    Instant createdAt
) {

    public static PaymentRequest create(
            Long gatheringId,
            BigDecimal totalAmount,
            BigDecimal amountPerPerson,
            int participantCount,
            Instant expiresAt
    ) {
        return new PaymentRequest(
            null,
            gatheringId,
            totalAmount,
            amountPerPerson,
            participantCount,
            expiresAt,
            null
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
