package com.paymeback.gathering.domain;

public enum GatheringStatus {

    ACTIVE("활성", "참여자 모집 중"),
    PAYMENT_REQUESTED("결제 요청", "결제 요청이 생성됨"),
    COMPLETED("완료", "모든 결제가 완료됨"),
    CLOSED("종료", "모임이 종료됨");

    private final String title;
    private final String description;

    GatheringStatus(String title, String description) {
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