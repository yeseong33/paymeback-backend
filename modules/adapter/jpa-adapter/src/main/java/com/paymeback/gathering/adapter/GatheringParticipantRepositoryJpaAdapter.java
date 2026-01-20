package com.paymeback.gathering.adapter;

import com.paymeback.gathering.domain.GatheringParticipant;
import com.paymeback.gathering.entity.GatheringParticipantEntity;
import com.paymeback.gathering.repository.GatheringJpaRepository;
import com.paymeback.gathering.repository.GatheringParticipantJpaRepository;
import com.paymeback.gathering.repository.GatheringParticipantRepositoryPort;
import com.paymeback.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GatheringParticipantRepositoryJpaAdapter implements GatheringParticipantRepositoryPort {

    private final GatheringParticipantJpaRepository participantRepo;
    private final GatheringJpaRepository gatheringRepo;
    private final UserJpaRepository userRepo;

    @Override
    public GatheringParticipant save(GatheringParticipant participant) {
        var gathering = gatheringRepo.findById(participant.gatheringId())
            .orElseThrow(() -> new IllegalArgumentException("Gathering not found: " + participant.gatheringId()));
        var user = userRepo.findById(participant.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + participant.userId()));

        GatheringParticipantEntity entity = GatheringParticipantJpaMapper.toEntity(participant, gathering, user);
        var saved = participantRepo.save(entity);
        return GatheringParticipantJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<GatheringParticipant> findById(Long id) {
        return participantRepo.findById(id).map(GatheringParticipantJpaMapper::toDomain);
    }

    @Override
    public Optional<GatheringParticipant> findByGatheringIdAndUserId(Long gatheringId, Long userId) {
        return participantRepo.findByGatheringIdAndUserId(gatheringId, userId)
            .map(GatheringParticipantJpaMapper::toDomain);
    }

    @Override
    public List<GatheringParticipant> findByGatheringIdOrderByJoinedAt(Long gatheringId) {
        return participantRepo.findByGatheringIdOrderByJoinedAt(gatheringId).stream()
            .map(GatheringParticipantJpaMapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByGatheringIdAndUserId(Long gatheringId, Long userId) {
        return participantRepo.existsByGatheringIdAndUserId(gatheringId, userId);
    }

    @Override
    public long countByGatheringId(Long gatheringId) {
        return participantRepo.countByGatheringId(gatheringId);
    }
}
