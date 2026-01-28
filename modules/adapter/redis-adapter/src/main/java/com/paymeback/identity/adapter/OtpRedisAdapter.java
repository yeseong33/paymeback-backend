package com.paymeback.identity.adapter;

import com.paymeback.cache.KeyValueStore;
import com.paymeback.identity.port.OtpStore;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpRedisAdapter implements OtpStore {

    private final KeyValueStore keyValueStore;

    private static final String OTP_PREFIX = "otp:";

    @Override
    public Optional<String> get(String email) {
        return keyValueStore.get(OTP_PREFIX + email);
    }

    @Override
    public void save(String email, String otpCode, Duration ttl) {
        keyValueStore.set(OTP_PREFIX + email, otpCode, ttl);
    }

    @Override
    public void delete(String email) {
        keyValueStore.delete(OTP_PREFIX + email);
    }
}
