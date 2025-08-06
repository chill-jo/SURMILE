package com.example.surveyapp.domain.user.controller;

import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.generator.UserFixtureGenerator;
import com.example.surveyapp.domain.user.controller.dto.UserRequestDto;
import com.example.surveyapp.domain.user.controller.dto.UserResponseDto;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.service.UserService;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.example.surveyapp.config.generator.UserFixtureGenerator.ID;
import static com.example.surveyapp.config.generator.UserFixtureGenerator.ROLE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller: User 컨트롤러 테스트")
@Import(TestMockBeans.class)
public class UserControllerTest extends WebMvcTestBase {
    @Autowired
    private UserService userService;

    private final User user = UserFixtureGenerator.generateUserFixture();
    UserResponseDto responseDto = UserResponseDto.from(user);

    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    @DisplayName("기능_테스트_User_조회_API를_호출하면_회원이_조회된다")
    public void User_조회_API를_호출하면_회원이_조회된다() throws Exception {
        // Given
        when(userService.getMyInfo(ID)).thenReturn(responseDto);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/my-page")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verify(userService, times(1)).getMyInfo(ID);

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
                .andExpect(jsonPath("$.data.name").value(user.getName()))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.data.userRole").value(user.getUserRole().name()));
    }

    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    @DisplayName("기능_테스트_User_수정_API를_호출하면_회원정보가_수정된다")
    public void User_수정_API를_호출하면_회원정보가_수정된다() throws Exception {
        // Given
        UserRequestDto dto = new UserRequestDto(
                "new@example.com",
                "newPw123!",
                "newName",
                "newNick");

        UserResponseDto updatedDto = UserResponseDto.builder()
                .id(ID)
                .email(dto.getEmail())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .userRole(ROLE)
                .build();

        when(userService.updateMyInfo(eq(ID), any(UserRequestDto.class))).thenReturn(updatedDto);

        // When
        ResultActions resultActions = mockMvc.perform(patch("/api/my-page")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // Then
        verify(userService, times(1)).updateMyInfo(eq(ID), any(UserRequestDto.class));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.data.name").value(dto.getName()))
                .andExpect(jsonPath("$.data.nickname").value(dto.getNickname()))
                .andExpect(jsonPath("$.data.userRole").value(ROLE.name()));
    }
}
