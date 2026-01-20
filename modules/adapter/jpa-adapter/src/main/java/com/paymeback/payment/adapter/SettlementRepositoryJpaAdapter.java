package com.paymeback.payment.adapter;

import com.paymeback.payment.domain.Settlement;
import com.paymeback.payment.domain.SettlementStatus;
import com.paymeback.payment.entity.SettlementEntity;
import com.paymeback.payment.repository.SettlementJpaRepository;
import com.paymeback.payment.repository.SettlementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementRepositoryJpaAdapter implements SettlementRepositoryPort {

    private final SettlementJpaRepository settlementRepo;

    @Override
    public Settlement save(Settlement settlement) {
        SettlementEntity entity;

        if (settlement.id() != null) {
            entity = settlementRepo.findById(settlement.id())
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found: " + settlement.id()));
            entity.updateFromDomain(
                settlement.gatheringId(),
                settlement.fromUserId(),
                settlement.toUserId(),
                settlement.amount(),
                settlement.status(),
                settlement.settledAt()
            );
        } else {
            entity = SettlementJpaMapper.toEntity(settlement);
        }

        var saved = settlementRepo.save(entity);
        return SettlementJpaMapper.toDomain(saved);
    }

    @Override
    public List<Settlement> saveAll(List<Settlement> settlements) {
        var entities = settlements.stream()
            .map(SettlementJpaMapper::toEntity)
            .toList();
        var saved = settlementRepo.saveAll(entities);
        return saved.stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Settlement> findById(Long id) {
        return settlementRepo.findById(id).map(SettlementJpaMapper::toDomain);
    }

    @Override
    public List<Settlement> findByGatheringId(Long gatheringId) {
        return settlementRepo.findByGatheringIdOrderByCreatedAtDesc(gatheringId).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Settlement> findByFromUserId(Long fromUserId) {
        return settlementRepo.findByFromUserIdOrderByCreatedAtDesc(fromUserId).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Settlement> findByToUserId(Long toUserId) {
        return settlementRepo.findByToUserIdOrderByCreatedAtDesc(toUserId).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Settlement> findByGatheringIdAndStatus(Long gatheringId, SettlementStatus status) {
        return settlementRepo.findByGatheringIdAndStatus(gatheringId, status).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void deleteByGatheringId(Long gatheringId) {
        settlementRepo.deleteByGatheringId(gatheringId);
    }
}
