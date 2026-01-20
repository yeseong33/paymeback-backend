package com.paymeback.payment.repository;

import com.paymeback.payment.entity.PaymentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRequestJpaRepository extends JpaRepository<PaymentRequestEntity, Long> {

    Optional<PaymentRequestEntity> findByGatheringId(Long gatheringId);

    @Query("SELECT pr FROM PaymentRequestEntity pr WHERE pr.expiresAt < :now")
    List<PaymentRequestEntity> findExpiredRequests(@Param("now") Instant now);
}
