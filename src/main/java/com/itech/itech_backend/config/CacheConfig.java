package com.itech.itech_backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Profile({"render", "test", "minimal"})
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .recordStats());
        
        // Set cache names for the render environment
        cacheManager.setCacheNames(Arrays.asList(
                "products", "categories", "vendors", "userProfiles", "searchResults"
        ));
        
        return cacheManager;
    }

    @Bean
    @Profile({"prod", "production"})
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // Default configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // Configure different TTLs for different cache names
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("products", defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put("categories", defaultConfig.entryTtl(Duration.ofHours(2)));
        configMap.put("vendors", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configMap.put("userProfiles", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configMap.put("searchResults", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
    }

    @Bean
    @Profile({"prod", "production"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use Jackson serializer for values
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        
        // Use String serializer for keys
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        
        return template;
    }

    @Bean
    public KeyGenerator customKeyGenerator() {
        return (target, method, params) -> {
            // Include class name, method name and parameters in the cache key
            String key = target.getClass().getSimpleName() + "_" + method.getName();
            if (params.length > 0) {
                key += "_" + Arrays.stream(params)
                        .map(Object::toString)
                        .collect(Collectors.joining("_"));
            }
            return key;
        };
    }

    @Bean
    public KeyGenerator queryKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder(target.getClass().getSimpleName())
                    .append("_")
                    .append(method.getName());

            // Add query parameters to key
            for (Object param : params) {
                if (param != null) {
                    key.append("_").append(param.toString().replaceAll("\\s+", ""));
                }
            }
            return key.toString();
        };
    }

    @Bean
    public KeyGenerator listKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder(target.getClass().getSimpleName())
                    .append("_")
                    .append(method.getName());

            // Add pagination and sorting parameters if present
            if (params.length > 0) {
                key.append("_page").append(params[0])
                   .append("_size").append(params[1]);
                if (params.length > 2 && params[2] != null) {
                    key.append("_sort").append(params[2]);
                }
            }
            return key.toString();
        };
    }
}
