package com.itech.itech_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpCacheService {

    public NoOpCacheService() {
        log.warn("Redis is disabled. Using No-Op cache service. Caching operations will be ignored.");
    }

    public void put(String key, Object value, Duration duration) {
        // No-op
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        return Optional.empty();
    }

    public void remove(String key) {
        // No-op
    }

    public boolean exists(String key) {
        return false;
    }

    // List operations
    public <T> void addToList(String key, T value) {
        // No-op
    }

    public <T> List<T> getList(String key, Class<T> type) {
        return new ArrayList<>();
    }

    // Hash operations
    public void putHash(String key, String hashKey, Object value) {
        // No-op
    }

    public <T> Optional<T> getHash(String key, String hashKey, Class<T> type) {
        return Optional.empty();
    }

    // Set operations
    public <T> void addToSet(String key, T value) {
        // No-op
    }

    public <T> Set<T> getSet(String key, Class<T> type) {
        return new HashSet<>();
    }

    // Atomic operations
    public Long increment(String key) {
        return 0L;
    }

    public Long increment(String key, long delta) {
        return 0L;
    }

    // Lock operations
    public boolean acquireLock(String lockKey, Duration timeout) {
        return true; // Always grant lock in no-op mode
    }

    public void releaseLock(String lockKey) {
        // No-op
    }

    // Batch operations
    public void putAll(Map<String, Object> keyValueMap, Duration duration) {
        // No-op
    }

    public List<Object> getAll(Collection<String> keys) {
        return new ArrayList<>();
    }

    // Pattern-based operations
    public Set<String> getKeysByPattern(String pattern) {
        return new HashSet<>();
    }

    public void deleteKeysByPattern(String pattern) {
        // No-op
    }

    // Cache statistics
    public Map<String, Long> getCacheStats(String pattern) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalKeys", 0L);
        stats.put("expiringKeys", 0L);
        return stats;
    }

    // Utility methods
    public void clearCache() {
        // No-op
    }

    public void refreshExpiry(String key, Duration duration) {
        // No-op
    }
}
