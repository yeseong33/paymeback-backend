package com.paymeback.payment.repository;

import com.paymeback.payment.domain.Settlement;
import com.paymeback.payment.domain.SettlementStatus;

import java.util.List;
import java.util.Optional;

public interface SettlementRepositoryPort {
    Settlement save(Settlement settlement);
    List<Settlement> saveAll(List<Settlement> settlements);
    Optional<Settlement> findById(Long id);
    List<Settlement> findByGatheringId(Long gatheringId);
    List<Settlement> findByFromUserId(Long fromUserId);
    List<Settlement> findByToUserId(Long toUserId);
    List<Settlement> findByGatheringIdAndStatus(Long gatheringId, SettlementStatus status);
    void deleteByGatheringId(Long gatheringId);
    void deleteByGatheringIdAndStatus(Long gatheringId, SettlementStatus status);
}
