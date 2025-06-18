package com.mymemo.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {    // Redis와의 연결을 위한 팩토리 객체 생성
        return new LettuceConnectionFactory();      // host/port는 application.yml에서 자동 주입   => application.yml에 설정한 host, port를 자동으로 읽는다.
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {      // 실제로 Redis에 데이터를 넣고 꺼낼 때 사용할 도구(빈)    / String -> String 구조로 key-value 저장소처럼 사용하겠다는 것
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}
