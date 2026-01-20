package com.paymeback.payment.entity;

import com.paymeback.gathering.entity.GatheringParticipantEntity;
import com.paymeback.payment.domain.PaymentStatus;
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
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private GatheringParticipantEntity participant;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String externalTransactionId;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant completedAt;

    private String failureReason;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @Builder
    private PaymentEntity(Long id, GatheringParticipantEntity participant, BigDecimal amount,
                          PaymentStatus status, String externalTransactionId,
                          Instant completedAt, String failureReason) {
        this.id = id;
        this.participant = participant;
        this.amount = amount;
        this.status = status != null ? status : PaymentStatus.PENDING;
        this.externalTransactionId = externalTransactionId;
        this.completedAt = completedAt;
        this.failureReason = failureReason;
    }

    public void updateFromDomain(PaymentStatus status, String externalTransactionId,
                                  Instant completedAt, String failureReason) {
        this.status = status;
        this.externalTransactionId = externalTransactionId;
        this.completedAt = completedAt;
        this.failureReason = failureReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity that = (PaymentEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
