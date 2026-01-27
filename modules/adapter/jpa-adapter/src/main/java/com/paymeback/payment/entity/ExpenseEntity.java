package com.paymeback.payment.entity;

import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.payment.domain.ExpenseCategory;
import com.paymeback.user.entity.UserEntity;
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
import java.util.ArrayList;
import java.util.List;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private GatheringEntity gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private UserEntity payer;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseParticipantEntity> participants = new ArrayList<>();

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
    private ExpenseEntity(GatheringEntity gathering, UserEntity payer, BigDecimal totalAmount,
        String description, String location, ExpenseCategory category,
        Instant paidAt, String receiptImageUrl) {
        this.gathering = gathering;
        this.payer = payer;
        this.totalAmount = totalAmount;
        this.description = description;
        this.location = location;
        this.category = category;
        this.paidAt = paidAt;
        this.receiptImageUrl = receiptImageUrl;
    }

    public Long getGatheringId() {
        return gathering != null ? gathering.getId() : null;
    }

    public Long getPayerId() {
        return payer != null ? payer.getId() : null;
    }

    public void updateFromDomain(BigDecimal totalAmount, String description, String location,
        ExpenseCategory category, Instant paidAt, String receiptImageUrl) {
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
