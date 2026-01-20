package com.paymeback.payment.domain;

public enum PaymentStatus {

    PENDING("대기", "결제 대기 중"),
    PROCESSING("처리중", "결제 처리 중"),
    COMPLETED("완료", "결제 완료"),
    FAILED("실패", "결제 실패"),
    CANCELLED("취소", "결제 취소");

    private final String title;
    private final String description;

    PaymentStatus(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
