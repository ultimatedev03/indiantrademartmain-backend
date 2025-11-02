package com.itech.itech_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true", matchIfMissing = false)
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CacheService(
        RedisTemplate<String, Object> redisTemplate,
        StringRedisTemplate stringRedisTemplate,
        ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public void put(String key, Object value, Duration duration) {
        if (value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // List operations
    public <T> void addToList(String key, T value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public <T> List<T> getList(String key, Class<T> type) {
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            return new ArrayList<>();
        }
        
        List<Object> values = redisTemplate.opsForList().range(key, 0, size - 1);
        return values.stream()
            .map(type::cast)
            .collect(Collectors.toList());
    }

    // Hash operations
    public void putHash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public <T> Optional<T> getHash(String key, String hashKey, Class<T> type) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }

    // Set operations
    public <T> void addToSet(String key, T value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public <T> Set<T> getSet(String key, Class<T> type) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members == null) {
            return new HashSet<>();
        }
        return members.stream()
            .map(type::cast)
            .collect(Collectors.toSet());
    }

    // Atomic operations
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // Lock operations
    public boolean acquireLock(String lockKey, Duration timeout) {
        return Boolean.TRUE.equals(
            stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "locked", timeout)
        );
    }

    public void releaseLock(String lockKey) {
        stringRedisTemplate.delete(lockKey);
    }

    // Batch operations
    public void putAll(Map<String, Object> keyValueMap, Duration duration) {
        redisTemplate.opsForValue().multiSet(keyValueMap);
        keyValueMap.keySet().forEach(key -> 
            redisTemplate.expire(key, duration)
        );
    }

    public List<Object> getAll(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    // Pattern-based operations
    public Set<String> getKeysByPattern(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    public void deleteKeysByPattern(String pattern) {
        Set<String> keys = getKeysByPattern(pattern);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // Cache statistics
    public Map<String, Long> getCacheStats(String pattern) {
        Set<String> keys = getKeysByPattern(pattern);
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("totalKeys", (long) keys.size());
        stats.put("expiringKeys", keys.stream()
            .filter(key -> redisTemplate.getExpire(key) > 0)
            .count());
            
        return stats;
    }

    // Utility methods
    public void clearCache() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void refreshExpiry(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }
}
