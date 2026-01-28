package com.paymeback.identity.port;

import java.time.Duration;
import java.util.Optional;

public interface OtpStore {

    Optional<String> get(String email);

    void save(String email, String otpCode, Duration ttl);

    void delete(String email);
}
