package com.paymeback.payment.entity;

import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.payment.domain.SettlementStatus;
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
import java.util.Objects;

@Entity
@Table(name = "settlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private GatheringEntity gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserEntity fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private UserEntity toUser;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant settledAt;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @Builder
    private SettlementEntity(GatheringEntity gathering, UserEntity fromUser, UserEntity toUser,
        BigDecimal amount, SettlementStatus status, Instant settledAt) {
        this.gathering = gathering;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.status = status != null ? status : SettlementStatus.PENDING;
        this.settledAt = settledAt;
    }

    public Long getGatheringId() {
        return gathering != null ? gathering.getId() : null;
    }

    public Long getFromUserId() {
        return fromUser != null ? fromUser.getId() : null;
    }

    public Long getToUserId() {
        return toUser != null ? toUser.getId() : null;
    }

    public void updateFromDomain(BigDecimal amount, SettlementStatus status, Instant settledAt) {
        this.amount = amount;
        this.status = status;
        this.settledAt = settledAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettlementEntity that = (SettlementEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
