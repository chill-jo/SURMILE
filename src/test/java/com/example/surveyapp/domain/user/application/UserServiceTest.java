package com.example.surveyapp.domain.user.application;

import com.example.surveyapp.domain.ai.moderation.application.facade.AiModerationFacade;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import com.example.surveyapp.domain.user.presentation.dto.UserRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.UserResponseDto;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Service: User 서비스 테스트")
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AiModerationFacade aiModerationFacade;

    @Test
    @DisplayName("기능_테스트_회원_정보를_조회한다")
    void 회원_정보를_조회한다(){
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
    @DisplayName("기능_테스트_회원_정보를_수정한다")
    void 회원_정보를_수정한다(){
        // Given
        User user = generateUserFixture();
        UserRequestDto requestDto = new UserRequestDto("new@example.com", "newPw123!", "NewName", "newNickname");

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(requestDto.getNickname())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPw123!");
        when(aiModerationFacade.checkNicknameModeration(eq("newNickname")))
                .thenReturn(AiModerationResult.of(null, AiModerationResultStatusEnum.APPROVED));

        // When
        UserResponseDto updatedUser = userService.updateMyInfo(ID, requestDto);

        // Then
        assertThat(updatedUser.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(updatedUser.getName()).isEqualTo(requestDto.getName());
        assertThat(updatedUser.getNickname()).isEqualTo(requestDto.getNickname());
    }

    @Test
    @DisplayName("예외_테스트_회원_정보_수정_도중_이메일이_중복되었다")
    void 회원_수정_도중_이메일이_중복되었다(){
        // Given
        User user = generateUserFixture();
        UserRequestDto requestDto = new UserRequestDto("duplicate@example.com", "password", "name", "nickname");

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateMyInfo(ID, requestDto))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.EXISTS_EMAIL.getMessage());
    }
}
