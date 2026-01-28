package com.paymeback.gathering.exception;

public class GatheringNotReadyForPaymentException extends GatheringException {

    public GatheringNotReadyForPaymentException() {
        super("결제 요청을 생성할 수 없는 상태입니다.");
    }
}
