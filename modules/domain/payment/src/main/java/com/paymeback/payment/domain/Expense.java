package com.paymeback.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Expense(
    Long id,
    Long gatheringId,
    Long payerId,
    BigDecimal totalAmount,
    String description,
    String location,
    ExpenseCategory category,
    Instant paidAt,
    String receiptImageUrl,
    Instant createdAt,
    Instant updatedAt
) {
    public static Expense create(
        Long gatheringId,
        Long payerId,
        BigDecimal totalAmount,
        String description,
        String location,
        ExpenseCategory category,
        Instant paidAt,
        String receiptImageUrl
    ) {
        Instant now = Instant.now();
        return new Expense(
            null,
            gatheringId,
            payerId,
            totalAmount,
            description,
            location,
            category,
            paidAt,
            receiptImageUrl,
            now,
            now
        );
    }
}
