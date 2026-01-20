package com.paymeback.gathering.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

public record Gathering(
    Long id,
    String title,
    String description,
    Long ownerId,
    String qrCode,
    Instant qrExpiresAt,
    GatheringStatus status,
    BigDecimal totalAmount,
    Instant scheduledAt,
    List<Long> participantIds,
    Instant createdAt,
    Instant updatedAt
) {

    public static Gathering create(String title, String description, Long ownerId, String qrCode, Instant qrExpiresAt) {
        return new Gathering(
            null,
            title,
            description,
            ownerId,
            qrCode,
            qrExpiresAt,
            GatheringStatus.ACTIVE,
            null,
            null,
            List.of(),
            null,
            null
        );
    }

    public Gathering withTotalAmount(BigDecimal totalAmount) {
        validatePaymentRequestCreation();
        return new Gathering(
            id, title, description, ownerId, qrCode, qrExpiresAt,
            GatheringStatus.PAYMENT_REQUESTED,
            totalAmount, scheduledAt, participantIds, createdAt, updatedAt
        );
    }

    public Gathering close() {
        return new Gathering(
            id, title, description, ownerId, qrCode, qrExpiresAt,
            GatheringStatus.CLOSED,
            totalAmount, scheduledAt, participantIds, createdAt, updatedAt
        );
    }

    public Gathering refreshQrCode(String newQrCode, Instant newExpiresAt) {
        if (this.status != GatheringStatus.ACTIVE) {
            throw new IllegalStateException("활성 상태가 아닌 모임의 QR 코드는 갱신할 수 없습니다.");
        }
        return new Gathering(
            id, title, description, ownerId, newQrCode, newExpiresAt,
            status, totalAmount, scheduledAt, participantIds, createdAt, updatedAt
        );
    }

    public Gathering update(String newTitle, String newDescription, Instant newScheduledAt) {
        return new Gathering(
            id,
            newTitle != null ? newTitle : title,
            newDescription != null ? newDescription : description,
            ownerId, qrCode, qrExpiresAt, status, totalAmount,
            newScheduledAt != null ? newScheduledAt : scheduledAt,
            participantIds, createdAt, updatedAt
        );
    }

    public boolean isQrCodeExpired() {
        return Instant.now().isAfter(qrExpiresAt);
    }

    public boolean canJoin() {
        return status == GatheringStatus.ACTIVE && !isQrCodeExpired();
    }

    public boolean isOwner(Long userId) {
        return ownerId.equals(userId);
    }

    public int getParticipantCount() {
        return participantIds != null ? participantIds.size() : 0;
    }

    public BigDecimal getAmountPerPerson() {
        if (totalAmount == null || getParticipantCount() == 0) {
            return BigDecimal.ZERO;
        }
        return totalAmount.divide(BigDecimal.valueOf(getParticipantCount()), 2, RoundingMode.HALF_UP);
    }

    private void validatePaymentRequestCreation() {
        if (status != GatheringStatus.ACTIVE) {
            throw new IllegalStateException("활성 상태가 아닌 모임에는 결제 요청을 생성할 수 없습니다.");
        }
        if (getParticipantCount() == 0) {
            throw new IllegalStateException("참여자가 없는 모임에는 결제 요청을 생성할 수 없습니다.");
        }
    }
}