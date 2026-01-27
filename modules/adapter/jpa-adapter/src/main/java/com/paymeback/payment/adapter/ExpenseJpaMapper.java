package com.paymeback.payment.adapter;

import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.entity.ExpenseEntity;
import com.paymeback.user.entity.UserEntity;

public final class ExpenseJpaMapper {

    private ExpenseJpaMapper() {
    }

    public static Expense toDomain(ExpenseEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Expense(
            entity.getId(),
            entity.getGatheringId(),
            entity.getPayerId(),
            entity.getTotalAmount(),
            entity.getDescription(),
            entity.getLocation(),
            entity.getCategory(),
            entity.getPaidAt(),
            entity.getReceiptImageUrl(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static ExpenseEntity toEntity(Expense domain, GatheringEntity gathering, UserEntity payer) {
        if (domain == null) {
            return null;
        }

        return ExpenseEntity.builder()
            .gathering(gathering)
            .payer(payer)
            .totalAmount(domain.totalAmount())
            .description(domain.description())
            .location(domain.location())
            .category(domain.category())
            .paidAt(domain.paidAt())
            .receiptImageUrl(domain.receiptImageUrl())
            .build();
    }
}
