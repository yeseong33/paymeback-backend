package com.paymeback.payment.entity;

import com.paymeback.payment.domain.ShareType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "expense_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ExpenseParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long expenseId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shareAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareType shareType;

    @Column(precision = 10, scale = 4)
    private BigDecimal shareValue;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @Builder
    private ExpenseParticipantEntity(Long expenseId, Long userId, BigDecimal shareAmount,
        ShareType shareType, BigDecimal shareValue) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
        this.shareType = shareType;
        this.shareValue = shareValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseParticipantEntity that = (ExpenseParticipantEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
