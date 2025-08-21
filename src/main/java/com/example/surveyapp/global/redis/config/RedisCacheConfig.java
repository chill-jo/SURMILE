package com.example.surveyapp.global.redis.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisCacheConfig {
	@Value("${spring.data.redis.default-ttl-sec}")
	private int defaultTtl;

	// Spring Cache를 Redis로 사용하기 위한 RedisCacheManager 설정
	@Bean
	public RedisCacheManager redisCacheManager(
		RedisConnectionFactory redisConnectionFactory,
		GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer
	) {
		/**
		 * Redis 캐시의 기본 정책 구성
		 * - key prefix
		 * - null 캐싱
		 * - TTL
		 * - 직렬화 방식
		 */
		// 저장될 캐시 데이터 설정
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.computePrefixWith(cacheName -> "cache:" + cacheName + ":")
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
			)
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer)
			)
			.entryTtl(Duration.ofSeconds(defaultTtl));

		return RedisCacheManager
			.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheConfiguration)
			.build();
	}
}
