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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(RedisTestContainersExtension.class)
@MockitoBean(types = {UserRepository.class, PasswordEncoder.class, JwtProvider.class, RedisTemplateFacade.class})
@ActiveProfiles("test")
@DisplayName("Redis::Auth")
public class RedisTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisTemplateFacade redisTemplateFacade;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("기능: 로그인을 성공한다")
    public void success_login() {

        // Given
        User user = UserFixtureGenerator.generateUserFixture();
        ReflectionTestUtils.setField(user, "id", 1L);
        LoginRequestDto requestDto = mock(LoginRequestDto.class);
        when(userRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.createAccessToken(user.getId(), user.getUserRole())).thenReturn("access_token");
        when(jwtProvider.createRefreshToken(user.getId())).thenReturn("refresh_token");

        // When
        LoginResponseDto result = userService.login(requestDto);

        // Then
        Assertions.assertThat(result).isInstanceOf(LoginResponseDto.class);

    }

    @Test
    @DisplayName("기능: 로그아웃을 성공한다")
    public void success_logout() {

        //Given
        String accessToken = "access_token";
        when(jwtProvider.extractUserId(jwtProvider.substringToken(accessToken))).thenReturn("1");

        //When
        userService.logout(accessToken);

        //Then
        System.out.println("테스트성공");
    }

    @Test
    @DisplayName("기능: 리프레시 토큰을 재발급 받는다")
    public void success_refreshToken() {

        //Given
        String refreshToken = "refresh_token";
        String userId = "1";
        User user = UserFixtureGenerator.generateUserFixture();
        ReflectionTestUtils.setField(user, "id", 1L);

        when(jwtProvider.substringToken(refreshToken)).thenReturn(refreshToken);
        when(jwtProvider.isValidRefreshToken(refreshToken)).thenReturn(true);
        when(jwtProvider.extractUserId(refreshToken)).thenReturn(userId);
        when(userRepository.findByIdAndIsDeletedFalse(Long.parseLong(userId))).thenReturn(Optional.of(user));
        when(jwtProvider.createAccessToken(user.getId(), user.getUserRole())).thenReturn("access_token");
        when(jwtProvider.createRefreshToken(user.getId())).thenReturn("refresh_token");


        //when
        LoginResponseDto result = userService.refresh(refreshToken);

        //then
        Assertions.assertThat(result).isInstanceOf(LoginResponseDto.class);
    }

}