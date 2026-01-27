package com.paymeback.payment.entity;

import com.paymeback.payment.domain.ShareType;
import com.paymeback.user.entity.UserEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private ExpenseEntity expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

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
    private ExpenseParticipantEntity(ExpenseEntity expense, UserEntity user, BigDecimal shareAmount,
        ShareType shareType, BigDecimal shareValue) {
        this.expense = expense;
        this.user = user;
        this.shareAmount = shareAmount;
        this.shareType = shareType;
        this.shareValue = shareValue;
    }

    public Long getExpenseId() {
        return expense != null ? expense.getId() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
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
