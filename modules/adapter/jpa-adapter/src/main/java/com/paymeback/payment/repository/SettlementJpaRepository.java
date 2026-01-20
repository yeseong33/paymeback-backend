package com.paymeback.payment.repository;

import com.paymeback.payment.domain.SettlementStatus;
import com.paymeback.payment.entity.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementJpaRepository extends JpaRepository<SettlementEntity, Long> {

    List<SettlementEntity> findByGatheringIdOrderByCreatedAtDesc(Long gatheringId);

    List<SettlementEntity> findByFromUserIdOrderByCreatedAtDesc(Long fromUserId);

    List<SettlementEntity> findByToUserIdOrderByCreatedAtDesc(Long toUserId);

    List<SettlementEntity> findByGatheringIdAndStatus(Long gatheringId, SettlementStatus status);

    void deleteByGatheringId(Long gatheringId);
}
