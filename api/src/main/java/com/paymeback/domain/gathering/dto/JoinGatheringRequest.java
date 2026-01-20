package com.paymeback.domain.gathering.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinGatheringRequest(

    @NotBlank(message = "QR 코드는 필수입니다.")
    String qrCode
) {
}