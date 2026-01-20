package com.paymeback.payment.adapter;

import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.entity.ExpenseEntity;

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

    public static ExpenseEntity toEntity(Expense domain) {
        if (domain == null) {
            return null;
        }

        return ExpenseEntity.builder()
            .gatheringId(domain.gatheringId())
            .payerId(domain.payerId())
            .totalAmount(domain.totalAmount())
            .description(domain.description())
            .location(domain.location())
            .category(domain.category())
            .paidAt(domain.paidAt())
            .receiptImageUrl(domain.receiptImageUrl())
            .build();
    }
}
