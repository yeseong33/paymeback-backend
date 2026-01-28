package com.paymeback.domain.gathering.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.gathering.dto.CreateGatheringRequest;
import com.paymeback.domain.gathering.dto.GatheringResponse;
import com.paymeback.domain.gathering.dto.ParticipantResponse;
import com.paymeback.domain.gathering.dto.UpdateGatheringRequest;
import com.paymeback.domain.user.service.UserService;
import com.paymeback.gathering.domain.Gathering;
import com.paymeback.gathering.domain.GatheringParticipant;
import com.paymeback.gathering.repository.GatheringParticipantRepositoryPort;
import com.paymeback.gathering.repository.GatheringRepositoryPort;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringService {

    private final GatheringRepositoryPort gatheringRepository;
    private final GatheringParticipantRepositoryPort participantRepository;
    private final QRCodeService qrCodeService;
    private final UserService userService;

    @Transactional
    public GatheringResponse createGathering(CreateGatheringRequest request, String userEmail) {
        User owner = userService.findByEmail(userEmail);

        // QR 코드 생성
        String qrCode = qrCodeService.generateQRCode();

        Instant startAt = request.startAt() != null ? Instant.ofEpochMilli(request.startAt()) : null;
        Instant endAt = request.endAt() != null ? Instant.ofEpochMilli(request.endAt()) : null;

        Gathering gathering = Gathering.create(
            request.title(),
            request.description(),
            owner.id(),
            qrCode,
            qrCodeService.calculateExpirationTime(),
            startAt,
            endAt
        );

        Gathering savedGathering = gatheringRepository.save(gathering);
        log.info("새 모임이 생성되었습니다. id: {}, owner: {}", savedGathering.id(), owner.email());

        return toGatheringResponse(savedGathering, owner);
    }

    @Transactional
    public GatheringResponse joinGathering(String qrCode, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = findByQrCode(qrCode);

        // 참여 가능 여부 검증
        validateJoinGathering(gathering, user);

        // 참여자 추가
        GatheringParticipant participant = GatheringParticipant.of(gathering.id(), user.id());
        participantRepository.save(participant);

        log.info("사용자가 모임에 참여했습니다. gathering: {}, user: {}", gathering.id(), user.email());

        // 최신 데이터로 다시 조회
        gathering = findById(gathering.id());
        return toGatheringResponseWithoutQrCode(gathering);
    }

    @Transactional
    public GatheringResponse createPaymentRequest(Long gatheringId, BigDecimal totalAmount, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = findById(gatheringId);

        gathering.validateOwner(user.id());

        Gathering updatedGathering = gathering.withTotalAmount(totalAmount);
        Gathering savedGathering = gatheringRepository.save(updatedGathering);

        log.info("결제 요청이 생성되었습니다. gathering: {}, totalAmount: {}", gatheringId, totalAmount);

        User owner = userService.findById(savedGathering.ownerId());
        return toGatheringResponse(savedGathering, owner);
    }

    public GatheringResponse getGathering(Long gatheringId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = findById(gatheringId);

        // 방장인 경우 QR 코드 포함, 아닌 경우 제외
        if (gathering.isOwner(user.id())) {
            User owner = userService.findById(gathering.ownerId());
            return toGatheringResponse(gathering, owner);
        } else {
            return toGatheringResponseWithoutQrCode(gathering);
        }
    }

    public Page<GatheringResponse> getMyGatherings(String userEmail, Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        Page<Gathering> gatherings = gatheringRepository.findByOwnerIdOrderByCreatedAtDesc(user.id(), pageable);
        return gatherings.map(g -> {
            User owner = userService.findById(g.ownerId());
            return toGatheringResponse(g, owner);
        });
    }

    public Page<GatheringResponse> getParticipatedGatherings(String userEmail, Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        Page<Gathering> gatherings = gatheringRepository.findByParticipantUserId(user.id(), pageable);
        return gatherings.map(this::toGatheringResponseWithoutQrCode);
    }

    @Transactional
    public void closeGathering(Long gatheringId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = findById(gatheringId);

        gathering.validateOwner(user.id());

        Gathering closedGathering = gathering.close();
        gatheringRepository.save(closedGathering);
        log.info("모임이 종료되었습니다. gathering: {}", gatheringId);
    }

    @Transactional
    public GatheringResponse refreshQRCode(Long gatheringId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = findById(gatheringId);

        gathering.validateOwner(user.id());

        String newQrCode = qrCodeService.generateQRCode();
        Gathering updatedGathering = gathering.refreshQrCode(newQrCode, qrCodeService.calculateExpirationTime());
        Gathering savedGathering = gatheringRepository.save(updatedGathering);

        log.info("QR 코드가 갱신되었습니다. gathering: {}", gatheringId);

        return toGatheringResponse(savedGathering, user);
    }

    @Transactional
    public GatheringResponse updateGathering(Long gatheringId, UpdateGatheringRequest request, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = findById(gatheringId);

        gathering.validateOwner(user.id());

        Instant startAt = request.startAt() != null ? Instant.ofEpochMilli(request.startAt()) : null;
        Instant endAt = request.endAt() != null ? Instant.ofEpochMilli(request.endAt()) : null;
        Gathering updatedGathering = gathering.update(request.title(), request.description(), startAt, endAt);
        Gathering savedGathering = gatheringRepository.save(updatedGathering);

        log.info("모임이 수정되었습니다. gathering: {}", gatheringId);

        return toGatheringResponse(savedGathering, user);
    }

    public Gathering findById(Long id) {
        return gatheringRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_FOUND));
    }

    public Gathering findByQrCode(String qrCode) {
        return gatheringRepository.findByQrCode(qrCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_FOUND));
    }

    public void existById(Long id) {
        if (!gatheringRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.GATHERING_NOT_FOUND);
        }
    }

    private void validateJoinGathering(Gathering gathering, User user) {
        gathering.validateJoinable();

        // 중복 참여 검증 (Repository 의존)
        if (participantRepository.existsByGatheringIdAndUserId(gathering.id(), user.id())) {
            throw new BusinessException(ErrorCode.ALREADY_PARTICIPATED);
        }
    }

    private GatheringResponse toGatheringResponse(Gathering gathering, User owner) {
        List<GatheringParticipant> participants = participantRepository.findByGatheringIdOrderByJoinedAt(gathering.id());
        List<ParticipantResponse> participantResponses = participants.stream()
            .map(p -> {
                User participantUser = userService.findById(p.userId());
                return ParticipantResponse.from(p, participantUser);
            })
            .toList();

        return GatheringResponse.from(gathering, owner, participantResponses);
    }

    private GatheringResponse toGatheringResponseWithoutQrCode(Gathering gathering) {
        User owner = userService.findById(gathering.ownerId());
        List<GatheringParticipant> participants = participantRepository.findByGatheringIdOrderByJoinedAt(gathering.id());
        List<ParticipantResponse> participantResponses = participants.stream()
            .map(p -> {
                User participantUser = userService.findById(p.userId());
                return ParticipantResponse.from(p, participantUser);
            })
            .toList();

        return GatheringResponse.withoutQrCode(gathering, owner, participantResponses);
    }
}
