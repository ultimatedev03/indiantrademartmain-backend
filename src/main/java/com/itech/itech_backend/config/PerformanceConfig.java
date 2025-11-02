package com.itech.itech_backend.config;

// Temporarily disabled Caffeine
// import com.github.benmanes.caffeine.cache.Caffeine;
// import org.springframework.cache.CacheManager;
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
// @EnableCaching  // Disabled to avoid conflicts with Redis cache
@EnableAsync
public class PerformanceConfig {

    // Temporarily disabled Caffeine cache manager to avoid conflicts
    // @Bean
    // public CacheManager cacheManager() {
    //     CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    //     cacheManager.setCaffeine(Caffeine.newBuilder()
    //             .expireAfterWrite(1, TimeUnit.HOURS)
    //             .maximumSize(1000)
    //             .recordStats());
    //     return cacheManager;
    // }

    @Bean(name = "asyncExecutor")
    @Profile({"!minimal"})
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "asyncExecutor")
    @Profile({"minimal"})
    public Executor minimalAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "backgroundTaskExecutor")
    @Profile({"!minimal"})
    public Executor backgroundTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("BackgroundTask-");
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "backgroundTaskExecutor")
    @Profile({"minimal"})
    public Executor minimalBackgroundTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(5);
        executor.setThreadNamePrefix("BackgroundTask-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "scheduledTaskExecutor")
    @Profile({"!minimal"})
    public Executor scheduledTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("ScheduledTask-");
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "scheduledTaskExecutor")
    @Profile({"minimal"})
    public Executor minimalScheduledTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(2);
        executor.setThreadNamePrefix("ScheduledTask-");
        executor.initialize();
        return executor;
    }
}
