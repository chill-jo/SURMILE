package com.example.surveyapp.global.redis.infrastructure;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisTemplateImpl implements RedisTemplate {

	private final org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

    @Override
	public <T> void write(String key, T value, Duration ttl) {
		try {
			String jsonString = objectMapper.writeValueAsString(value);
			redisTemplate.opsForValue().set(key, jsonString, ttl);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(key + " : " + value);
		}
	}

    @Override
	public <T> T read(String key, Class<T> type) {
		String jsonString = redisTemplate.opsForValue().get(key);

		if (jsonString == null) {
			return null;
		}

		try {
			return objectMapper.readValue(jsonString, type);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(key + " : " + type.getName());
		}
	}

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

	//    @Override
	//    @Cacheable(cacheNames = "refreshToken", key = "#userId")
	//    public String saveRefreshToken(Long userId, String token) {
	//        return token;
	//    }
	//
	//    @Override
	//    @Cacheable(cacheNames = "accessToken", key = "#userId")
	//    public String saveExpiredAccessToken(Long userId, String token) {
	//        return token;
	//    }
	//
	//    @Override
	//    @Cacheable(cacheNames = "refreshToken", key = "#userId", unless = "#result == null")
	//    public String existRefreshToken(Long userId, String token) {
	//        return token;
	//    }
	//
	//    @Override
	//    @Cacheable(cacheNames = "accessToken", key = "#userId", unless = "#result == null")
	//    public String existExpiredAccessToken(Long userId, String token) {
	//        return token;
	//    }
	//
	//    @Override
	//    @CachePut(cacheNames = "refreshToken", key = "#userId")
	//    public String updateRefreshToken(Long userId, String token) {
	//        return token;
	//    }

}
