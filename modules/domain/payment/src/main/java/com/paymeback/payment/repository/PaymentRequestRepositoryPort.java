package com.paymeback.payment.repository;

import com.paymeback.payment.domain.PaymentRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRequestRepositoryPort {

    PaymentRequest save(PaymentRequest paymentRequest);

    Optional<PaymentRequest> findById(Long id);

    Optional<PaymentRequest> findByGatheringId(Long gatheringId);

    List<PaymentRequest> findExpiredRequests(Instant now);
}
