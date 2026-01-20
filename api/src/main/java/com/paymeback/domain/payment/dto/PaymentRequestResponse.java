package com.paymeback.domain.payment.dto;


import com.paymeback.payment.domain.PaymentRequest;
import java.math.BigDecimal;

public record PaymentRequestResponse(
    Long id,
    Long gatheringId,
    BigDecimal totalAmount,
    BigDecimal amountPerPerson,
    int participantCount,
    Long expiresAt,
    Long createdAt
) {

    public static PaymentRequestResponse from(PaymentRequest paymentRequest) {
        return new PaymentRequestResponse(
            paymentRequest.id(),
            paymentRequest.gatheringId(),
            paymentRequest.totalAmount(),
            paymentRequest.amountPerPerson(),
            paymentRequest.participantCount(),
            paymentRequest.expiresAt() != null ? paymentRequest.expiresAt().toEpochMilli() : null,
            paymentRequest.createdAt() != null ? paymentRequest.createdAt().toEpochMilli() : null
        );
    }
}