package com.paymeback.domain.payment.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.payment.dto.PaymentRequestResponse;
import com.paymeback.gathering.domain.Gathering;
import com.paymeback.gathering.domain.GatheringParticipant;
import com.paymeback.gathering.repository.GatheringParticipantRepositoryPort;
import com.paymeback.payment.domain.Payment;
import com.paymeback.payment.domain.PaymentRequest;
import com.paymeback.payment.repository.PaymentRepositoryPort;
import com.paymeback.payment.repository.PaymentRequestRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentRequestService {

    private final PaymentRequestRepositoryPort paymentRequestRepository;
    private final PaymentRepositoryPort paymentRepository;
    private final GatheringParticipantRepositoryPort participantRepository;
    private final NotificationService notificationService;

    @Value("${app.payment.timeout-hours:24}")
    private int paymentTimeoutHours;

    @Transactional
    public PaymentRequestResponse createPaymentRequest(Gathering gathering) {
        paymentRequestRepository.findByGatheringId(gathering.id())
            .ifPresent(existing -> {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 결제 요청이 존재합니다.");
            });

        List<GatheringParticipant> participants = participantRepository.findByGatheringIdOrderByJoinedAt(gathering.id());

        Instant expiresAt = Instant.now().plus(paymentTimeoutHours, ChronoUnit.HOURS);
        PaymentRequest paymentRequest = PaymentRequest.create(
            gathering.id(),
            gathering.totalAmount(),
            gathering.getAmountPerPerson(),
            participants.size(),
            expiresAt
        );
        PaymentRequest savedRequest = paymentRequestRepository.save(paymentRequest);

        createPaymentsForParticipants(participants, gathering.getAmountPerPerson());

        notificationService.sendPaymentRequestNotification(gathering);

        log.info("결제 요청이 생성되었습니다. gathering: {}, expiresAt: {}", gathering.id(), expiresAt);

        return PaymentRequestResponse.from(savedRequest);
    }

    public PaymentRequestResponse getPaymentRequest(Long gatheringId) {
        PaymentRequest paymentRequest = paymentRequestRepository.findByGatheringId(gatheringId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_REQUEST_NOT_FOUND));

        if (paymentRequest.isExpired()) {
            throw new BusinessException(ErrorCode.PAYMENT_EXPIRED);
        }

        return PaymentRequestResponse.from(paymentRequest);
    }

    @Transactional
    public void cancelExpiredPaymentRequests() {
        List<PaymentRequest> expiredRequests = paymentRequestRepository.findExpiredRequests(Instant.now());

        for (PaymentRequest request : expiredRequests) {
            List<Payment> payments = paymentRepository.findByGatheringId(request.gatheringId());
            for (Payment payment : payments) {
                if (!payment.isCompleted()) {
                    Payment cancelled = payment.markAsCancelled();
                    paymentRepository.save(cancelled);
                }
            }
            log.info("만료된 결제 요청이 취소되었습니다. request: {}", request.id());
        }
    }

    private void createPaymentsForParticipants(List<GatheringParticipant> participants, BigDecimal amountPerPerson) {
        for (GatheringParticipant participant : participants) {
            boolean hasPayment = paymentRepository.findById(participant.id()).isPresent();
            if (!hasPayment) {
                Payment payment = Payment.create(participant.id(), amountPerPerson);
                paymentRepository.save(payment);
            }
        }
    }
}
