package com.paymeback.domain.gathering.service;

import com.paymeback.common.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final QRCodeGenerator qrCodeGenerator;

    @Value("${app.qr-code.expiration-minutes:30}")
    private int qrExpirationMinutes;

    public String generateQRCode() {
        return UUID.randomUUID().toString();
    }

    public Instant calculateExpirationTime() {
        return Instant.now().plus(qrExpirationMinutes, ChronoUnit.MINUTES);
    }

    public byte[] generateQRCodeImage(String qrCode) {
        return qrCodeGenerator.generateQRCode(qrCode);
    }
}