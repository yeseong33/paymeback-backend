package com.paymeback.payment.repository;

import com.paymeback.payment.domain.Payment;
import com.paymeback.payment.domain.PaymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByExternalTransactionId(String externalTransactionId);

    List<Payment> findByGatheringId(Long gatheringId);

    List<Payment> findExpiredPayments(PaymentStatus status, Instant createdBefore);

    long countByGatheringIdAndStatus(Long gatheringId, PaymentStatus status);
}
