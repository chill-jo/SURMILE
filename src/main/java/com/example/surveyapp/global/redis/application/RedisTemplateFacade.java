package com.example.surveyapp.global.redis.application;

import java.time.Duration;

public interface RedisTemplateFacade {

	<T> void write(String key, T value, Duration ttl);

	<T> T read(String key, Class<T> type);

}
