package com.paymeback.payment.repository;

import com.paymeback.payment.domain.PaymentStatus;
import com.paymeback.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByExternalTransactionId(String externalTransactionId);

    @Query("SELECT p FROM PaymentEntity p JOIN p.participant pp WHERE pp.gathering.id = :gatheringId")
    List<PaymentEntity> findByGatheringId(@Param("gatheringId") Long gatheringId);

    @Query("SELECT p FROM PaymentEntity p WHERE p.status = :status AND p.createdAt < :createdBefore")
    List<PaymentEntity> findExpiredPayments(@Param("status") PaymentStatus status, @Param("createdBefore") Instant createdBefore);

    @Query("SELECT COUNT(p) FROM PaymentEntity p JOIN p.participant pp WHERE pp.gathering.id = :gatheringId AND p.status = :status")
    long countByGatheringIdAndStatus(@Param("gatheringId") Long gatheringId, @Param("status") PaymentStatus status);
}
