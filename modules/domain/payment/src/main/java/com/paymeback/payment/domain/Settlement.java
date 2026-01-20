package com.paymeback.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Settlement(
    Long id,
    Long gatheringId,
    Long fromUserId,
    Long toUserId,
    BigDecimal amount,
    SettlementStatus status,
    Instant settledAt,
    Instant createdAt,
    Instant updatedAt
) {
    public static Settlement create(
        Long gatheringId,
        Long fromUserId,
        Long toUserId,
        BigDecimal amount
    ) {
        Instant now = Instant.now();
        return new Settlement(
            null,
            gatheringId,
            fromUserId,
            toUserId,
            amount,
            SettlementStatus.PENDING,
            null,
            now,
            now
        );
    }

    public Settlement complete() {
        return new Settlement(
            id,
            gatheringId,
            fromUserId,
            toUserId,
            amount,
            SettlementStatus.COMPLETED,
            Instant.now(),
            createdAt,
            Instant.now()
        );
    }

    public Settlement confirm() {
        return new Settlement(
            id,
            gatheringId,
            fromUserId,
            toUserId,
            amount,
            SettlementStatus.CONFIRMED,
            settledAt,
            createdAt,
            Instant.now()
        );
    }
}
