package com.paymeback.gathering.domain;

import com.paymeback.gathering.exception.GatheringNotJoinableException;
import com.paymeback.gathering.exception.GatheringNotReadyForPaymentException;
import com.paymeback.gathering.exception.NotGatheringOwnerException;
import com.paymeback.gathering.exception.QrCodeExpiredException;

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
    Instant startAt,
    Instant endAt,
    List<Long> participantIds,
    Instant createdAt,
    Instant updatedAt
) {

    public static Gathering create(String title, String description, Long ownerId, String qrCode,
                                    Instant qrExpiresAt, Instant startAt, Instant endAt) {
        return new Gathering(
            null,
            title,
            description,
            ownerId,
            qrCode,
            qrExpiresAt,
            GatheringStatus.ACTIVE,
            null,
            startAt,
            endAt,
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
            totalAmount, startAt, endAt, participantIds, createdAt, updatedAt
        );
    }

    public Gathering close() {
        return new Gathering(
            id, title, description, ownerId, qrCode, qrExpiresAt,
            GatheringStatus.CLOSED,
            totalAmount, startAt, endAt, participantIds, createdAt, updatedAt
        );
    }

    public Gathering refreshQrCode(String newQrCode, Instant newExpiresAt) {
        if (this.status != GatheringStatus.ACTIVE) {
            throw new GatheringNotReadyForPaymentException();
        }
        return new Gathering(
            id, title, description, ownerId, newQrCode, newExpiresAt,
            status, totalAmount, startAt, endAt, participantIds, createdAt, updatedAt
        );
    }

    public Gathering update(String newTitle, String newDescription, Instant newStartAt, Instant newEndAt) {
        return new Gathering(
            id,
            newTitle != null ? newTitle : title,
            newDescription != null ? newDescription : description,
            ownerId, qrCode, qrExpiresAt, status, totalAmount,
            newStartAt != null ? newStartAt : startAt,
            newEndAt != null ? newEndAt : endAt,
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

    public void validateOwner(Long userId) {
        if (!isOwner(userId)) {
            throw new NotGatheringOwnerException();
        }
    }

    public void validateJoinable() {
        if (isQrCodeExpired()) {
            throw new QrCodeExpiredException();
        }
        if (status != GatheringStatus.ACTIVE) {
            throw new GatheringNotJoinableException();
        }
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
            throw new GatheringNotReadyForPaymentException();
        }
        if (getParticipantCount() == 0) {
            throw new GatheringNotReadyForPaymentException();
        }
    }
}