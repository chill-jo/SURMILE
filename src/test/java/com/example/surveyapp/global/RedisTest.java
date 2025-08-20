package com.example.surveyapp.global;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.example.surveyapp.domain.user.application.UserService;
import com.example.surveyapp.domain.user.presentation.dto.LoginRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.LoginResponseDto;
import com.example.surveyapp.global.redis.application.RedisTemplateFacade;

@SpringBootTest
@ExtendWith(RedisTestContainersExtension.class)
@ActiveProfiles("test")
@DisplayName("Redis::Auth")
public class RedisTest {

	@Autowired
	private RedisTemplateFacade redisTemplateFacade;

	@Autowired
	private UserService userService;

	@Test
	@Sql("classpath:test-auth.sql")
	@DisplayName("기능: 로그인을 성공한다")
	public void success_login() {

		// Given
		LoginRequestDto requestDto = new LoginRequestDto("test@example.com", "password123!");

		// When
		LoginResponseDto result = assertDoesNotThrow(() -> userService.login(requestDto));

		// Then
		assertThat(result).isInstanceOf(LoginResponseDto.class);
	}

	@Test
	@DisplayName("기능: 로그아웃을 성공한다")
	public void success_logout() {
		//Given
		String accessToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIyIiwicm9sZSI6IlNVUlZFWU9SIiwiaWF0IjoxNzU1NTE4MzM5LCJleHAiOjE3NTU1MjAxMzl9.73bn6HxpToCns02lstvHmKUUntyY3OLRKuxkZCKT9b7y6jPnVqdUHZPjPZ4evu7p";

		//When
		userService.logout(accessToken);

		//Then
		String actual = redisTemplateFacade.read("accessToken:2", String.class);
		assertThat(actual).isEqualTo(accessToken);
	}

	@Test
	//    @Sql("classpath:test-auth.sql")
	@DisplayName("기능: 리프레시 토큰을 재발급 받는다")
	public void success_refreshToken() {

		//Given
		String refreshToken = "refresh_token";

		//when
		LoginResponseDto result = userService.refresh(refreshToken);

		//then
		assertThat(result).isInstanceOf(LoginResponseDto.class);
	}

}
