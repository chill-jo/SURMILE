package com.example.surveyapp.global;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.presentation.dto.RegisterRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
	@DisplayName("기능: 로그인을 성공한다")
	public void success_login() {

		// Given
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("test1@example.com", "Password!234", "Password!234", "test1", "test1", UserRoleEnum.SURVEYOR);
        userService.register(registerRequestDto);
		LoginRequestDto requestDto = new LoginRequestDto("test1@example.com", "Password!234");

		// When
		LoginResponseDto result = assertDoesNotThrow(() -> userService.login(requestDto));

		// Then
		assertThat(result).isInstanceOf(LoginResponseDto.class);
	}

	@Test
	@DisplayName("기능: 로그아웃을 성공한다")
	public void success_logout() {
		//Given
		String accessToken = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIyIiwicm9sZSI6IlNVUlZFWU9SIiwiaWF0IjoxNzU1NjgyMzQwLCJleHAiOjE3NTU2ODQxNDB9.cz32waE8lqiQQ7sK6igrNVpZzyNZ4_1iVm_OACeWIOrTsAsBrTI9N9U_t6u2eEg6";

		//When
		userService.logout(accessToken);

		//Then
		String actual = redisTemplateFacade.read("accessToken:2", String.class);
		assertThat("Bearer " + actual).isEqualTo(accessToken);
	}

	@Test
	@DisplayName("기능: 리프레시 토큰을 재발급 받는다")
	public void success_refreshToken() {

		//Given
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("test2@example.com", "Password!234", "Password!234", "test2", "test2", UserRoleEnum.SURVEYOR);
        userService.register(registerRequestDto);
        LoginRequestDto requestDto = new LoginRequestDto("test2@example.com", "Password!234");
        LoginResponseDto loginResponseDto = userService.login(requestDto);
		String refreshToken = loginResponseDto.getRefreshToken();

		//when
		LoginResponseDto result = userService.refresh(refreshToken);

		//then
		assertThat(result).isInstanceOf(LoginResponseDto.class);
	}

}
