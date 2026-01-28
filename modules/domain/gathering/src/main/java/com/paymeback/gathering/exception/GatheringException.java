package com.paymeback.gathering.exception;

public abstract class GatheringException extends RuntimeException {

    protected GatheringException(String message) {
        super(message);
    }
}
