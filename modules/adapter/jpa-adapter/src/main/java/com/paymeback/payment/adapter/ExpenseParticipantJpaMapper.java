package com.paymeback.payment.adapter;

import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.entity.ExpenseParticipantEntity;

public final class ExpenseParticipantJpaMapper {

    private ExpenseParticipantJpaMapper() {
    }

    public static ExpenseParticipant toDomain(ExpenseParticipantEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ExpenseParticipant(
            entity.getId(),
            entity.getExpenseId(),
            entity.getUserId(),
            entity.getShareAmount(),
            entity.getShareType(),
            entity.getShareValue(),
            entity.getCreatedAt()
        );
    }

    public static ExpenseParticipantEntity toEntity(ExpenseParticipant domain) {
        if (domain == null) {
            return null;
        }

        return ExpenseParticipantEntity.builder()
            .expenseId(domain.expenseId())
            .userId(domain.userId())
            .shareAmount(domain.shareAmount())
            .shareType(domain.shareType())
            .shareValue(domain.shareValue())
            .build();
    }
}
