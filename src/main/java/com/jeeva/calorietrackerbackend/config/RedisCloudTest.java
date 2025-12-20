//package com.jeeva.calorietrackerbackend.config;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RedisCloudTest implements CommandLineRunner {
//
//    private final StringRedisTemplate redisTemplate;
//
//    public RedisCloudTest(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public void run(String... args) {
//        redisTemplate.opsForValue().set("cloud:test", "CONNECTED");
//        System.out.println(redisTemplate.opsForValue().get("cloud:test"));
//    }
//}