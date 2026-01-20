package com.paymeback.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseParticipant(
    Long id,
    Long expenseId,
    Long userId,
    BigDecimal shareAmount,
    ShareType shareType,
    BigDecimal shareValue,
    Instant createdAt
) {
    public static ExpenseParticipant create(
        Long expenseId,
        Long userId,
        BigDecimal shareAmount,
        ShareType shareType,
        BigDecimal shareValue
    ) {
        return new ExpenseParticipant(
            null,
            expenseId,
            userId,
            shareAmount,
            shareType,
            shareValue,
            Instant.now()
        );
    }
}
