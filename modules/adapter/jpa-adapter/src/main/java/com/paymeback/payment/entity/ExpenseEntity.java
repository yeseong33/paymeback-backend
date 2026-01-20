package com.paymeback.payment.entity;

import com.paymeback.payment.domain.ExpenseCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long gatheringId;

    @Column(nullable = false)
    private Long payerId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant paidAt;

    private String receiptImageUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @Builder
    private ExpenseEntity(Long gatheringId, Long payerId, BigDecimal totalAmount,
        String description, String location, ExpenseCategory category,
        Instant paidAt, String receiptImageUrl) {
        this.gatheringId = gatheringId;
        this.payerId = payerId;
        this.totalAmount = totalAmount;
        this.description = description;
        this.location = location;
        this.category = category;
        this.paidAt = paidAt;
        this.receiptImageUrl = receiptImageUrl;
    }

    public void updateFromDomain(Long gatheringId, Long payerId, BigDecimal totalAmount,
        String description, String location, ExpenseCategory category,
        Instant paidAt, String receiptImageUrl) {
        this.gatheringId = gatheringId;
        this.payerId = payerId;
        this.totalAmount = totalAmount;
        this.description = description;
        this.location = location;
        this.category = category;
        this.paidAt = paidAt;
        this.receiptImageUrl = receiptImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseEntity that = (ExpenseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
