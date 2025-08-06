package com.example.surveyapp.domain.user.service;

import com.example.surveyapp.domain.ai.moderation.config.ModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.nickname.controller.dto.NicknameModerationResponseDto;
import com.example.surveyapp.domain.ai.moderation.service.ModerationService;
import com.example.surveyapp.domain.user.controller.dto.UserRequestDto;
import com.example.surveyapp.domain.user.controller.dto.UserResponseDto;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.surveyapp.config.generator.UserFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModerationService moderationService;

    @Test
    void 유저_정보를_조회한다(){
        // Given
        when(userRepository.findById(ID)).thenReturn(Optional.of(generateUserFixture()));

        // When
        UserResponseDto result = userService.getMyInfo(ID);

        // Then
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getNickname()).isEqualTo(NICKNAME);
        assertThat(result.getUserRole()).isEqualTo(ROLE);
    }

    @Test
    void 유저_정보를_수정한다(){
        // Given
        User user = generateUserFixture();
        UserRequestDto requestDto = new UserRequestDto("new@example.com", "newPw123!", "NewName", "newNickname");

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(requestDto.getNickname())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPw123!");
        when(moderationService.moderate(any()))
                .thenReturn(new NicknameModerationResponseDto(
                        ModerationResultStatusEnum.APPROVED,
                        "적합한 닉네임입니다."
                ));

        // When
        UserResponseDto updatedUser = userService.updateMyInfo(ID, requestDto);

        // Then
        assertThat(updatedUser.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(updatedUser.getName()).isEqualTo(requestDto.getName());
        assertThat(updatedUser.getNickname()).isEqualTo(requestDto.getNickname());
    }

    @Test
    void 유저_수정_도중_이메일이_중복되었다(){
        // Given
        User user = generateUserFixture();
        UserRequestDto requestDto = new UserRequestDto("duplicate@example.com", "password", "name", "nickname");

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateMyInfo(ID, requestDto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EXISTS_EMAIL.getMessage());
    }
}
