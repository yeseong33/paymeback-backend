package com.paymeback.domain.payment.dto;


import com.paymeback.payment.domain.Payment;
import com.paymeback.payment.domain.PaymentStatus;
import java.math.BigDecimal;

public record PaymentResponse(
    Long id,
    Long participantId,
    BigDecimal amount,
    PaymentStatus status,
    String externalTransactionId,
    Long completedAt,
    String failureReason,
    Long createdAt
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.id(),
            payment.participantId(),
            payment.amount(),
            payment.status(),
            payment.externalTransactionId(),
            payment.completedAt() != null ? payment.completedAt().toEpochMilli() : null,
            payment.failureReason(),
            payment.createdAt() != null ? payment.createdAt().toEpochMilli() : null
        );
    }
}