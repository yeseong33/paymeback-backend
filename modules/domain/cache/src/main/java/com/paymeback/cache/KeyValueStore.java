package com.paymeback.cache;

import java.time.Duration;
import java.util.Optional;

/**
 * Key-Value 저장소 포트 인터페이스.
 * 구현체는 Redis, In-Memory 등 다양한 저장소를 사용할 수 있음.
 */
public interface KeyValueStore {

    Optional<String> get(String key);

    void set(String key, String value, Duration ttl);

    void delete(String key);
}