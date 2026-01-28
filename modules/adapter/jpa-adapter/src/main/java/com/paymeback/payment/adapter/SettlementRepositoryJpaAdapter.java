package com.paymeback.payment.adapter;

import com.paymeback.gathering.repository.GatheringJpaRepository;
import com.paymeback.payment.domain.Settlement;
import com.paymeback.payment.domain.SettlementStatus;
import com.paymeback.payment.entity.SettlementEntity;
import com.paymeback.payment.repository.SettlementJpaRepository;
import com.paymeback.payment.repository.SettlementRepositoryPort;
import com.paymeback.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementRepositoryJpaAdapter implements SettlementRepositoryPort {

    private final SettlementJpaRepository settlementRepo;
    private final GatheringJpaRepository gatheringRepo;
    private final UserJpaRepository userRepo;

    @Override
    public Settlement save(Settlement settlement) {
        SettlementEntity entity;

        if (settlement.id() != null) {
            entity = settlementRepo.findById(settlement.id())
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found: " + settlement.id()));
            entity.updateFromDomain(
                settlement.amount(),
                settlement.status(),
                settlement.settledAt()
            );
        } else {
            var gathering = gatheringRepo.findById(settlement.gatheringId())
                .orElseThrow(() -> new IllegalArgumentException("Gathering not found: " + settlement.gatheringId()));
            var fromUser = userRepo.findById(settlement.fromUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + settlement.fromUserId()));
            var toUser = userRepo.findById(settlement.toUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + settlement.toUserId()));
            entity = SettlementJpaMapper.toEntity(settlement, gathering, fromUser, toUser);
        }

        var saved = settlementRepo.save(entity);
        return SettlementJpaMapper.toDomain(saved);
    }

    @Override
    public List<Settlement> saveAll(List<Settlement> settlements) {
        var entities = settlements.stream()
            .map(s -> {
                var gathering = gatheringRepo.findById(s.gatheringId())
                    .orElseThrow(() -> new IllegalArgumentException("Gathering not found: " + s.gatheringId()));
                var fromUser = userRepo.findById(s.fromUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + s.fromUserId()));
                var toUser = userRepo.findById(s.toUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + s.toUserId()));
                return SettlementJpaMapper.toEntity(s, gathering, fromUser, toUser);
            })
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
        return settlementRepo.findByGathering_IdOrderByCreatedAtDesc(gatheringId).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Settlement> findByFromUserId(Long fromUserId) {
        return settlementRepo.findByFromUser_IdOrderByCreatedAtDesc(fromUserId).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Settlement> findByToUserId(Long toUserId) {
        return settlementRepo.findByToUser_IdOrderByCreatedAtDesc(toUserId).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Settlement> findByGatheringIdAndStatus(Long gatheringId, SettlementStatus status) {
        return settlementRepo.findByGathering_IdAndStatus(gatheringId, status).stream()
            .map(SettlementJpaMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void deleteByGatheringId(Long gatheringId) {
        settlementRepo.deleteByGathering_Id(gatheringId);
    }

    @Override
    @Transactional
    public void deleteByGatheringIdAndStatus(Long gatheringId, SettlementStatus status) {
        settlementRepo.deleteByGathering_IdAndStatus(gatheringId, status);
    }
}
