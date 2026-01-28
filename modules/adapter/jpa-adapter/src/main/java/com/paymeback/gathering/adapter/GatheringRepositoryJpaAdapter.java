package com.paymeback.gathering.adapter;

import com.paymeback.gathering.domain.Gathering;
import com.paymeback.gathering.domain.GatheringStatus;
import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.gathering.repository.GatheringJpaRepository;
import com.paymeback.gathering.repository.GatheringRepositoryPort;
import com.paymeback.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GatheringRepositoryJpaAdapter implements GatheringRepositoryPort {

    private final GatheringJpaRepository gatheringRepo;
    private final UserJpaRepository userRepo;

    @Override
    public Gathering save(Gathering gathering) {
        GatheringEntity entity;

        if (gathering.id() != null) {
            // Update existing
            entity = gatheringRepo.findById(gathering.id())
                .orElseThrow(() -> new IllegalArgumentException("Gathering not found: " + gathering.id()));
            entity.updateFromDomain(
                gathering.title(),
                gathering.description(),
                gathering.qrCode(),
                gathering.qrExpiresAt(),
                gathering.status(),
                gathering.totalAmount(),
                gathering.startAt(),
                gathering.endAt()
            );
        } else {
            // Create new
            var owner = userRepo.findById(gathering.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + gathering.ownerId()));
            entity = GatheringJpaMapper.toEntity(gathering, owner);
        }

        var saved = gatheringRepo.save(entity);
        return GatheringJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Gathering> findById(Long id) {
        return gatheringRepo.findById(id).map(GatheringJpaMapper::toDomain);
    }

    @Override
    public Optional<Gathering> findByQrCode(String qrCode) {
        return gatheringRepo.findByQrCode(qrCode).map(GatheringJpaMapper::toDomain);
    }

    @Override
    public Page<Gathering> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable) {
        return gatheringRepo.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable)
            .map(GatheringJpaMapper::toDomain);
    }

    @Override
    public Page<Gathering> findByStatusOrderByCreatedAtDesc(GatheringStatus status, Pageable pageable) {
        return gatheringRepo.findByStatusOrderByCreatedAtDesc(status, pageable)
            .map(GatheringJpaMapper::toDomain);
    }

    @Override
    public Page<Gathering> findByParticipantUserId(Long userId, Pageable pageable) {
        return gatheringRepo.findByParticipantUserId(userId, pageable)
            .map(GatheringJpaMapper::toDomain);
    }

    @Override
    public long countByOwnerIdAndStatus(Long ownerId, GatheringStatus status) {
        return gatheringRepo.countByOwnerIdAndStatus(ownerId, status);
    }

    @Override
    public boolean existsById(Long id) {
        return gatheringRepo.existsById(id);
    }
}
