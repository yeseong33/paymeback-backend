package com.paymeback.gathering.repository;

import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.gathering.entity.GatheringParticipantEntity;
import com.paymeback.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GatheringParticipantJpaRepository extends JpaRepository<GatheringParticipantEntity, Long> {

    Optional<GatheringParticipantEntity> findByGatheringAndUser(GatheringEntity gathering, UserEntity user);

    Optional<GatheringParticipantEntity> findByGatheringIdAndUserId(Long gatheringId, Long userId);

    List<GatheringParticipantEntity> findByGatheringOrderByJoinedAt(GatheringEntity gathering);

    List<GatheringParticipantEntity> findByGatheringIdOrderByJoinedAt(Long gatheringId);

    boolean existsByGatheringAndUser(GatheringEntity gathering, UserEntity user);

    boolean existsByGatheringIdAndUserId(Long gatheringId, Long userId);

    long countByGathering(GatheringEntity gathering);

    long countByGatheringId(Long gatheringId);
}
