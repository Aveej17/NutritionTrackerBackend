package com.jeeva.calorietrackerbackend.config;

import com.jeeva.calorietrackerbackend.util.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimitFilter rateLimitFilter(StringRedisTemplate redisTemplate,
                                           JwtUtil jwtUtil) {
        return new RateLimitFilter(redisTemplate, jwtUtil);
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(
            RateLimitFilter rateLimitFilter) {

        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitFilter);
        registration.setEnabled(false);
        return registration;
    }

}
