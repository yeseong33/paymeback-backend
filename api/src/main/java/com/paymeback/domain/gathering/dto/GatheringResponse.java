package com.paymeback.domain.gathering.dto;

import com.paymeback.gathering.domain.Gathering;
import com.paymeback.gathering.domain.GatheringStatus;
import com.paymeback.user.domain.User;
import com.paymeback.domain.user.dto.UserResponse;

import java.math.BigDecimal;
import java.util.List;

public record GatheringResponse(
    Long id,
    String title,
    String description,
    UserResponse owner,
    String qrCode,
    Long qrExpiresAt,
    GatheringStatus status,
    BigDecimal totalAmount,
    BigDecimal amountPerPerson,
    int participantCount,
    List<ParticipantResponse> participants,
    Long startAt,
    Long endAt,
    Long createdAt
) {

    public static GatheringResponse from(Gathering gathering, User owner, List<ParticipantResponse> participants) {
        return new GatheringResponse(
            gathering.id(),
            gathering.title(),
            gathering.description(),
            UserResponse.from(owner),
            gathering.qrCode(),
            gathering.qrExpiresAt() != null ? gathering.qrExpiresAt().toEpochMilli() : null,
            gathering.status(),
            gathering.totalAmount(),
            gathering.getAmountPerPerson(),
            participants.size(),
            participants,
            gathering.startAt() != null ? gathering.startAt().toEpochMilli() : null,
            gathering.endAt() != null ? gathering.endAt().toEpochMilli() : null,
            gathering.createdAt() != null ? gathering.createdAt().toEpochMilli() : null
        );
    }

    public static GatheringResponse withoutQrCode(Gathering gathering, User owner, List<ParticipantResponse> participants) {
        return new GatheringResponse(
            gathering.id(),
            gathering.title(),
            gathering.description(),
            UserResponse.from(owner),
            null, // QR 코드 숨김
            gathering.qrExpiresAt() != null ? gathering.qrExpiresAt().toEpochMilli() : null,
            gathering.status(),
            gathering.totalAmount(),
            gathering.getAmountPerPerson(),
            participants.size(),
            participants,
            gathering.startAt() != null ? gathering.startAt().toEpochMilli() : null,
            gathering.endAt() != null ? gathering.endAt().toEpochMilli() : null,
            gathering.createdAt() != null ? gathering.createdAt().toEpochMilli() : null
        );
    }
}
