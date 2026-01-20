package com.paymeback.domain.payment.service;

import com.paymeback.gathering.domain.Gathering;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 알림 발송 서비스
 * 실제 환경에서는 FCM, SMS 등 다양한 알림 채널을 사용할 수 있습니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Async
    public void sendPaymentRequestNotification(Gathering gathering) {
        // 참여자들에게 알림을 보내려면 별도로 참여자 정보를 조회해야 합니다.
        // 현재는 로깅만 수행합니다.
        log.info("결제 요청 알림 발송 예정 - gathering: {}", gathering.id());
    }

    @Async
    public void sendPaymentCompletionNotification(User owner, User payer, Gathering gathering) {
        try {
            sendPaymentCompletionEmail(owner, payer, gathering);
            log.info("결제 완료 알림 발송 완료 - owner: {}, payer: {}, gathering: {}",
                owner.email(), payer.email(), gathering.id());
        } catch (Exception e) {
            log.error("결제 완료 알림 발송 실패 - owner: {}, payer: {}, gathering: {}",
                owner.email(), payer.email(), gathering.id(), e);
        }
    }

    private void sendPaymentCompletionEmail(User owner, User payer, Gathering gathering) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(owner.email());
        message.setSubject(String.format("[Dutch Pay] %s - 결제 완료 알림", gathering.title()));
        message.setText(String.format(
            "안녕하세요, %s님!\n\n" +
                "%s님이 모임 '%s'에 대한 결제를 완료했습니다.\n\n" +
                "결제 금액: %s원\n\n" +
                "Dutch Pay 앱에서 전체 결제 현황을 확인하실 수 있습니다.\n\n" +
                "감사합니다.",
            owner.name(),
            payer.name(),
            gathering.title(),
            gathering.getAmountPerPerson()
        ));

        mailSender.send(message);
    }
}
