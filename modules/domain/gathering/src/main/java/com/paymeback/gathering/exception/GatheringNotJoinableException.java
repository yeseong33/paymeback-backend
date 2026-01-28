package com.paymeback.gathering.exception;

public class GatheringNotJoinableException extends GatheringException {

    public GatheringNotJoinableException() {
        super("참여할 수 없는 모임입니다.");
    }
}
