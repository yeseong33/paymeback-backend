package com.paymeback.payment.adapter;

import com.paymeback.gathering.repository.GatheringParticipantJpaRepository;
import com.paymeback.payment.domain.Payment;
import com.paymeback.payment.domain.PaymentStatus;
import com.paymeback.payment.entity.PaymentEntity;
import com.paymeback.payment.repository.PaymentJpaRepository;
import com.paymeback.payment.repository.PaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryJpaAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final GatheringParticipantJpaRepository participantJpaRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity;

        if (payment.id() != null) {
            entity = paymentJpaRepository.findById(payment.id())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + payment.id()));
            entity.updateFromDomain(
                payment.status(),
                payment.externalTransactionId(),
                payment.completedAt(),
                payment.failureReason()
            );
        } else {
            var participant = participantJpaRepository.findById(payment.participantId())
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + payment.participantId()));
            entity = PaymentJpaMapper.toEntity(payment, participant);
        }

        var saved = paymentJpaRepository.save(entity);
        return PaymentJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id).map(PaymentJpaMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByExternalTransactionId(String externalTransactionId) {
        return paymentJpaRepository.findByExternalTransactionId(externalTransactionId)
            .map(PaymentJpaMapper::toDomain);
    }

    @Override
    public List<Payment> findByGatheringId(Long gatheringId) {
        return paymentJpaRepository.findByGatheringId(gatheringId).stream()
            .map(PaymentJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Payment> findExpiredPayments(PaymentStatus status, Instant createdBefore) {
        return paymentJpaRepository.findExpiredPayments(status, createdBefore).stream()
            .map(PaymentJpaMapper::toDomain)
            .toList();
    }

    @Override
    public long countByGatheringIdAndStatus(Long gatheringId, PaymentStatus status) {
        return paymentJpaRepository.countByGatheringIdAndStatus(gatheringId, status);
    }
}
