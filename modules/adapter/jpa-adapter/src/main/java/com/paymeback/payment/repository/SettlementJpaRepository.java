package com.paymeback.payment.repository;

import com.paymeback.payment.domain.SettlementStatus;
import com.paymeback.payment.entity.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementJpaRepository extends JpaRepository<SettlementEntity, Long> {

    List<SettlementEntity> findByGathering_IdOrderByCreatedAtDesc(Long gatheringId);

    List<SettlementEntity> findByFromUser_IdOrderByCreatedAtDesc(Long fromUserId);

    List<SettlementEntity> findByToUser_IdOrderByCreatedAtDesc(Long toUserId);

    List<SettlementEntity> findByGathering_IdAndStatus(Long gatheringId, SettlementStatus status);

    void deleteByGathering_Id(Long gatheringId);
}
