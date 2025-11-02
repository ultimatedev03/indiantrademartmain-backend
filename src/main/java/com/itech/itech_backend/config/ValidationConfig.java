package com.itech.itech_backend.config;

import com.itech.itech_backend.interceptor.RequestValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ValidationConfig implements WebMvcConfigurer {

    private final RequestValidationInterceptor requestValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestValidationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health/**", "/api/metrics/**");
    }

    @Bean
    public static MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
