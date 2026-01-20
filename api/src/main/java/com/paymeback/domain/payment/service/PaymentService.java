package com.paymeback.domain.payment.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.gathering.service.GatheringService;
import com.paymeback.domain.payment.dto.PaymentResponse;
import com.paymeback.domain.payment.dto.ProcessPaymentRequest;
import com.paymeback.domain.user.service.UserService;
import com.paymeback.gathering.domain.Gathering;
import com.paymeback.gathering.domain.GatheringParticipant;
import com.paymeback.gathering.repository.GatheringParticipantRepositoryPort;
import com.paymeback.payment.domain.Payment;
import com.paymeback.payment.repository.PaymentRepositoryPort;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepositoryPort paymentRepository;
    private final GatheringParticipantRepositoryPort participantRepository;
    private final GatheringService gatheringService;
    private final UserService userService;
    private final ExternalPaymentService externalPaymentService;
    private final NotificationService notificationService;

    @Transactional
    public PaymentResponse processPayment(Long gatheringId, ProcessPaymentRequest request, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = gatheringService.findById(gatheringId);

        GatheringParticipant participant = participantRepository.findByGatheringIdAndUserId(gatheringId, user.id())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "모임 참여자를 찾을 수 없습니다."));

        Payment payment = paymentRepository.findById(participant.id()).orElse(null);
        if (payment == null) {
            payment = Payment.create(participant.id(), gathering.getAmountPerPerson());
            payment = paymentRepository.save(payment);
        }

        if (!payment.canProcess()) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }

        try {
            String transactionId = externalPaymentService.processPayment(
                payment.amount(),
                request.paymentMethod(),
                request.cardNumber(),
                request.expiryDate(),
                request.cvv()
            );

            payment = payment.markAsProcessing(transactionId);
            payment = payment.markAsCompleted();
            payment = paymentRepository.save(payment);

            log.info("결제가 완료되었습니다. payment: {}, transactionId: {}", payment.id(), transactionId);

            User owner = userService.findById(gathering.ownerId());
            notificationService.sendPaymentCompletionNotification(owner, user, gathering);

            return PaymentResponse.from(payment);

        } catch (Exception e) {
            payment = payment.markAsFailed(e.getMessage());
            paymentRepository.save(payment);
            log.error("결제 처리 중 오류가 발생했습니다. payment: {}", payment.id(), e);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, e.getMessage());
        }
    }

    public PaymentResponse getPaymentStatus(Long gatheringId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = gatheringService.findById(gatheringId);

        GatheringParticipant participant = participantRepository.findByGatheringIdAndUserId(gatheringId, user.id())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "모임 참여자를 찾을 수 없습니다."));

        Payment payment = paymentRepository.findById(participant.id()).orElse(null);
        if (payment == null) {
            payment = Payment.create(participant.id(), gathering.getAmountPerPerson());
            payment = paymentRepository.save(payment);
        }

        return PaymentResponse.from(payment);
    }

    public List<PaymentResponse> getGatheringPayments(Long gatheringId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Gathering gathering = gatheringService.findById(gatheringId);

        if (!gathering.isOwner(user.id())) {
            throw new BusinessException(ErrorCode.NOT_GATHERING_OWNER);
        }

        List<Payment> payments = paymentRepository.findByGatheringId(gatheringId);
        return payments.stream()
            .map(PaymentResponse::from)
            .toList();
    }
}
