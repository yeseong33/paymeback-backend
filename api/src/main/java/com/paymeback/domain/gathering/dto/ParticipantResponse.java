package com.paymeback.domain.gathering.dto;

import com.paymeback.gathering.domain.GatheringParticipant;
import com.paymeback.payment.domain.SettlementStatus;
import com.paymeback.user.domain.User;
import com.paymeback.domain.user.dto.UserResponse;

public record ParticipantResponse(
    Long id,
    UserResponse user,
    SettlementStatus settlementStatus,
    Long joinedAt
) {

    public static ParticipantResponse from(GatheringParticipant participant, User user) {
        return new ParticipantResponse(
            participant.id(),
            UserResponse.from(user),
            SettlementStatus.PENDING,
            participant.joinedAt() != null ? participant.joinedAt().toEpochMilli() : null
        );
    }

    public static ParticipantResponse from(GatheringParticipant participant, User user, SettlementStatus settlementStatus) {
        return new ParticipantResponse(
            participant.id(),
            UserResponse.from(user),
            settlementStatus,
            participant.joinedAt() != null ? participant.joinedAt().toEpochMilli() : null
        );
    }
}
