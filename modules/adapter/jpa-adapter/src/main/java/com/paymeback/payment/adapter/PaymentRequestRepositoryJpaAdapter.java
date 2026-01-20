package com.paymeback.payment.adapter;

import com.paymeback.gathering.repository.GatheringJpaRepository;
import com.paymeback.payment.domain.PaymentRequest;
import com.paymeback.payment.entity.PaymentRequestEntity;
import com.paymeback.payment.repository.PaymentRequestJpaRepository;
import com.paymeback.payment.repository.PaymentRequestRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRequestRepositoryJpaAdapter implements PaymentRequestRepositoryPort {

    private final PaymentRequestJpaRepository paymentRequestJpaRepository;
    private final GatheringJpaRepository gatheringJpaRepository;

    @Override
    public PaymentRequest save(PaymentRequest paymentRequest) {
        PaymentRequestEntity entity;

        if (paymentRequest.id() != null) {
            entity = paymentRequestJpaRepository.findById(paymentRequest.id())
                .orElseThrow(() -> new IllegalArgumentException("PaymentRequest not found: " + paymentRequest.id()));
        } else {
            var gathering = gatheringJpaRepository.findById(paymentRequest.gatheringId())
                .orElseThrow(() -> new IllegalArgumentException("Gathering not found: " + paymentRequest.gatheringId()));
            entity = PaymentRequestJpaMapper.toEntity(paymentRequest, gathering);
        }

        var saved = paymentRequestJpaRepository.save(entity);
        return PaymentRequestJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<PaymentRequest> findById(Long id) {
        return paymentRequestJpaRepository.findById(id).map(PaymentRequestJpaMapper::toDomain);
    }

    @Override
    public Optional<PaymentRequest> findByGatheringId(Long gatheringId) {
        return paymentRequestJpaRepository.findByGatheringId(gatheringId)
            .map(PaymentRequestJpaMapper::toDomain);
    }

    @Override
    public List<PaymentRequest> findExpiredRequests(Instant now) {
        return paymentRequestJpaRepository.findExpiredRequests(now).stream()
            .map(PaymentRequestJpaMapper::toDomain)
            .toList();
    }
}
