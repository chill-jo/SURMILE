package com.example.surveyapp.global.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();

		// 단일 Redis 서버 설정
		config.useSingleServer()
			.setAddress("redis://" + redisHost + ":" + redisPort);

		return Redisson.create(config);
	}

}
