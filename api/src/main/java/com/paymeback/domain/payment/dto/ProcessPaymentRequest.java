package com.paymeback.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record ProcessPaymentRequest(

    @NotBlank(message = "결제 수단은 필수입니다.")
    String paymentMethod,

    String cardNumber,
    String expiryDate,
    String cvv
) {
}