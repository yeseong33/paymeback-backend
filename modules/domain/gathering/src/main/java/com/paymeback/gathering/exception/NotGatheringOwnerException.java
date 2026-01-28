package com.paymeback.gathering.exception;

public class NotGatheringOwnerException extends GatheringException {

    public NotGatheringOwnerException() {
        super("모임 방장이 아닙니다.");
    }
}
