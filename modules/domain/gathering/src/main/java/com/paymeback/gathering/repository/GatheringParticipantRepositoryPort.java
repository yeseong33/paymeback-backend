package com.paymeback.gathering.repository;

import com.paymeback.gathering.domain.GatheringParticipant;

import java.util.List;
import java.util.Optional;

public interface GatheringParticipantRepositoryPort {

    GatheringParticipant save(GatheringParticipant participant);

    Optional<GatheringParticipant> findById(Long id);

    Optional<GatheringParticipant> findByGatheringIdAndUserId(Long gatheringId, Long userId);

    List<GatheringParticipant> findByGatheringIdOrderByJoinedAt(Long gatheringId);

    boolean existsByGatheringIdAndUserId(Long gatheringId, Long userId);

    long countByGatheringId(Long gatheringId);
}
