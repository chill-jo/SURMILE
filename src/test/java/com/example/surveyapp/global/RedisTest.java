package com.example.surveyapp.global;

import com.example.surveyapp.config.generator.UserFixtureGenerator;
import com.example.surveyapp.domain.user.application.UserService;
import com.example.surveyapp.domain.user.application.provider.JwtProvider;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.domain.user.presentation.dto.LoginRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.LoginResponseDto;
import com.example.surveyapp.global.common.redis.application.RedisTemplateFacade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(RedisTestContainersExtension.class)
@ActiveProfiles("test")
@DisplayName("Redis::Auth")
public class RedisTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

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
        LoginResponseDto result = userService.login(requestDto);

        // Then
        Assertions.assertThat(result).isInstanceOf(LoginResponseDto.class);

    }

    @Test
    @DisplayName("기능: 로그아웃을 성공한다")
    public void success_logout() {

        //Given
        String accessToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIyIiwicm9sZSI6IlNVUlZFWU9SIiwiaWF0IjoxNzU1NTE4MzM5LCJleHAiOjE3NTU1MjAxMzl9.73bn6HxpToCns02lstvHmKUUntyY3OLRKuxkZCKT9b7y6jPnVqdUHZPjPZ4evu7p";

        //When
        userService.logout(accessToken);

        //Then
        System.out.println(redisTemplateFacade.read("accessToken:2", String.class));
    }

    @Test
    @Sql("classpath:test-auth.sql")
    @DisplayName("기능: 리프레시 토큰을 재발급 받는다")
    public void success_refreshToken() {

        //Given
        String refreshToken = "refresh_token";

        //when
        LoginResponseDto result = userService.refresh(refreshToken);

        //then
        Assertions.assertThat(result).isInstanceOf(LoginResponseDto.class);
    }

}