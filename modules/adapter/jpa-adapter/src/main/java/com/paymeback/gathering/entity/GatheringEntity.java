package com.paymeback.gathering.entity;

import com.paymeback.gathering.domain.GatheringStatus;
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
@Table(name = "gatherings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class GatheringEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false, unique = true)
    private String qrCode;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant qrExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatheringStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant scheduledAt;

    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GatheringParticipantEntity> participants = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @Builder
    private GatheringEntity(String title, String description, UserEntity owner, String qrCode,
        Instant qrExpiresAt, GatheringStatus status, BigDecimal totalAmount, Instant scheduledAt) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.qrCode = qrCode;
        this.qrExpiresAt = qrExpiresAt;
        this.status = status != null ? status : GatheringStatus.ACTIVE;
        this.totalAmount = totalAmount;
        this.scheduledAt = scheduledAt;
    }

    public void updateFromDomain(String title, String description, String qrCode,
        Instant qrExpiresAt, GatheringStatus status, BigDecimal totalAmount, Instant scheduledAt) {
        this.title = title;
        this.description = description;
        this.qrCode = qrCode;
        this.qrExpiresAt = qrExpiresAt;
        this.status = status;
        this.totalAmount = totalAmount;
        this.scheduledAt = scheduledAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GatheringEntity that = (GatheringEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
