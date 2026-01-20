package com.paymeback.domain.gathering.dto;

import com.paymeback.gathering.domain.GatheringParticipant;
import com.paymeback.payment.domain.PaymentStatus;
import com.paymeback.user.domain.User;
import com.paymeback.domain.user.dto.UserResponse;

public record ParticipantResponse(
    Long id,
    UserResponse user,
    PaymentStatus paymentStatus,
    Long joinedAt
) {

    public static ParticipantResponse from(GatheringParticipant participant, User user) {
        return new ParticipantResponse(
            participant.id(),
            UserResponse.from(user),
            PaymentStatus.PENDING, // 결제 상태는 별도 조회 필요
            participant.joinedAt() != null ? participant.joinedAt().toEpochMilli() : null
        );
    }

    public static ParticipantResponse from(GatheringParticipant participant, User user, PaymentStatus paymentStatus) {
        return new ParticipantResponse(
            participant.id(),
            UserResponse.from(user),
            paymentStatus,
            participant.joinedAt() != null ? participant.joinedAt().toEpochMilli() : null
        );
    }
}
