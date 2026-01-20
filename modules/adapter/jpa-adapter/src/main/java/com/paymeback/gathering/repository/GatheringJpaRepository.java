package com.paymeback.gathering.repository;

import com.paymeback.gathering.domain.GatheringStatus;
import com.paymeback.gathering.entity.GatheringEntity;
import com.paymeback.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GatheringJpaRepository extends JpaRepository<GatheringEntity, Long> {

    Optional<GatheringEntity> findByQrCode(String qrCode);

    Page<GatheringEntity> findByOwnerOrderByCreatedAtDesc(UserEntity owner, Pageable pageable);

    Page<GatheringEntity> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    Page<GatheringEntity> findByStatusOrderByCreatedAtDesc(GatheringStatus status, Pageable pageable);

    @Query("SELECT g FROM GatheringEntity g JOIN g.participants p WHERE p.user.id = :userId ORDER BY g.createdAt DESC")
    Page<GatheringEntity> findByParticipantUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(g) FROM GatheringEntity g WHERE g.owner.id = :ownerId AND g.status = :status")
    long countByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") GatheringStatus status);
}
