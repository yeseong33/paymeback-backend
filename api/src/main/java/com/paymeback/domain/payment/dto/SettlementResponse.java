package com.paymeback.domain.payment.dto;

import com.paymeback.domain.user.dto.UserResponse;
import com.paymeback.payment.domain.Settlement;
import com.paymeback.payment.domain.SettlementStatus;
import com.paymeback.user.domain.User;

import java.math.BigDecimal;

public record SettlementResponse(
    Long id,
    Long gatheringId,
    UserResponse fromUser,
    UserResponse toUser,
    BigDecimal amount,
    SettlementStatus status,
    Long settledAt,
    Long createdAt
) {

    public static SettlementResponse from(Settlement settlement, User fromUser, User toUser) {
        return new SettlementResponse(
            settlement.id(),
            settlement.gatheringId(),
            UserResponse.from(fromUser),
            UserResponse.from(toUser),
            settlement.amount(),
            settlement.status(),
            settlement.settledAt() != null ? settlement.settledAt().toEpochMilli() : null,
            settlement.createdAt() != null ? settlement.createdAt().toEpochMilli() : null
        );
    }
}
