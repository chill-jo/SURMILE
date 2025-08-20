package com.example.surveyapp.global.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisSerializerConfig {

	// Cache value 값 직렬화/역직렬화를 위한 설정 커스터마이징
	@Bean
	public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
		ObjectMapper objectMapper = new ObjectMapper();

		// value 데이터타입 역직렬화 지원
		objectMapper.activateDefaultTyping(
			LaissezFaireSubTypeValidator.instance,
			ObjectMapper.DefaultTyping.NON_FINAL
		);

		// LocalDateTime 직렬화 지원
		objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 직렬화 지원
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}
}
