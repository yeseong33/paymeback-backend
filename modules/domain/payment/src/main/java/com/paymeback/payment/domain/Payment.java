package com.paymeback.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Payment(
    Long id,
    Long participantId,
    BigDecimal amount,
    PaymentStatus status,
    String externalTransactionId,
    Instant completedAt,
    String failureReason,
    Instant createdAt,
    Instant updatedAt
) {

    public static Payment create(Long participantId, BigDecimal amount) {
        return new Payment(
            null,
            participantId,
            amount,
            PaymentStatus.PENDING,
            null,
            null,
            null,
            null,
            null
        );
    }

    public Payment markAsProcessing(String externalTransactionId) {
        return new Payment(
            id, participantId, amount,
            PaymentStatus.PROCESSING,
            externalTransactionId,
            completedAt, failureReason, createdAt, updatedAt
        );
    }

    public Payment markAsCompleted() {
        return new Payment(
            id, participantId, amount,
            PaymentStatus.COMPLETED,
            externalTransactionId,
            Instant.now(),
            null,
            createdAt, updatedAt
        );
    }

    public Payment markAsFailed(String reason) {
        return new Payment(
            id, participantId, amount,
            PaymentStatus.FAILED,
            externalTransactionId,
            null,
            reason,
            createdAt, updatedAt
        );
    }

    public Payment markAsCancelled() {
        return new Payment(
            id, participantId, amount,
            PaymentStatus.CANCELLED,
            externalTransactionId,
            null,
            "결제 시간 만료",
            createdAt, updatedAt
        );
    }

    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean canProcess() {
        return status == PaymentStatus.PENDING;
    }
}
