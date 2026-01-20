package com.paymeback.gathering.repository;

import com.paymeback.gathering.domain.Gathering;
import com.paymeback.gathering.domain.GatheringStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GatheringRepositoryPort {

    Gathering save(Gathering gathering);

    Optional<Gathering> findById(Long id);

    Optional<Gathering> findByQrCode(String qrCode);

    Page<Gathering> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    Page<Gathering> findByStatusOrderByCreatedAtDesc(GatheringStatus status, Pageable pageable);

    Page<Gathering> findByParticipantUserId(Long userId, Pageable pageable);

    long countByOwnerIdAndStatus(Long ownerId, GatheringStatus status);
}
