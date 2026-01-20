package com.paymeback.domain.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 외부 결제 API 연동 서비스 (Mock 구현)
 * 실제 환경에서는 PG사 API를 연동합니다.
 */
@Slf4j
@Service
public class ExternalPaymentService {

    public String processPayment(BigDecimal amount, String paymentMethod,
        String cardNumber, String expiryDate, String cvv) {

        log.info("결제 처리 시작 - 금액: {}, 결제수단: {}", amount, paymentMethod);

        // Mock 결제 처리 로직
        validatePaymentInfo(paymentMethod, cardNumber, expiryDate, cvv);

        // 결제 성공 시뮬레이션 (90% 성공률)
        if (Math.random() < 0.9) {
            String transactionId = generateTransactionId();
            log.info("결제 성공 - 거래번호: {}", transactionId);
            return transactionId;
        } else {
            log.error("결제 실패 - 카드 승인 거부");
            throw new RuntimeException("카드 승인이 거부되었습니다.");
        }
    }

    public void cancelPayment(String transactionId) {
        log.info("결제 취소 - 거래번호: {}", transactionId);

        // Mock 취소 처리
        if (Math.random() < 0.95) {
            log.info("결제 취소 성공 - 거래번호: {}", transactionId);
        } else {
            log.error("결제 취소 실패 - 거래번호: {}", transactionId);
            throw new RuntimeException("결제 취소에 실패했습니다.");
        }
    }

    public boolean verifyPayment(String transactionId) {
        log.info("결제 검증 - 거래번호: {}", transactionId);

        // Mock 검증 로직 (95% 성공률)
        boolean isValid = Math.random() < 0.95;
        log.info("결제 검증 결과: {} - 거래번호: {}", isValid, transactionId);

        return isValid;
    }

    private void validatePaymentInfo(String paymentMethod, String cardNumber, String expiryDate, String cvv) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("결제 수단이 필요합니다.");
        }

        if ("CARD".equals(paymentMethod)) {
            if (cardNumber == null || cardNumber.length() < 16) {
                throw new IllegalArgumentException("유효하지 않은 카드 번호입니다.");
            }
            if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
                throw new IllegalArgumentException("유효하지 않은 유효기간입니다.");
            }
            if (cvv == null || cvv.length() < 3) {
                throw new IllegalArgumentException("유효하지 않은 CVV입니다.");
            }
        }
    }

    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}