package com.paymeback.gathering.exception;

public class QrCodeExpiredException extends GatheringException {

    public QrCodeExpiredException() {
        super("QR코드가 만료되었습니다.");
    }
}
