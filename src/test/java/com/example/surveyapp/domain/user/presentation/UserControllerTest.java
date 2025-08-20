package com.example.surveyapp.domain.user.presentation;

import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.generator.UserFixtureGenerator;
import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import com.example.surveyapp.domain.user.presentation.dto.*;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.application.UserService;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.example.surveyapp.config.generator.UserFixtureGenerator.ID;
import static com.example.surveyapp.config.generator.UserFixtureGenerator.ROLE;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        ReflectionTestUtils.setField(user, "id", 1L);
        UserResponseDto responseDto = UserResponseDto.from(user);

        when(userService.getMyInfo(ID)).thenReturn(responseDto);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/my-page")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        verify(userService, times(1)).getMyInfo(ID);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
                .andExpect(jsonPath("$.data.name").value(user.getName()))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.data.userRole").value(user.getUserRole().name()))
                .andDo(document("user/get-my-page",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 ID"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.userRole").type(JsonFieldType.STRING).description("유저 권한")
                        )
                ))
        ;
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // Then
        verify(userService, times(1)).updateMyInfo(eq(ID), any(UserRequestDto.class));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.data.name").value(dto.getName()))
                .andExpect(jsonPath("$.data.nickname").value(dto.getNickname()))
                .andExpect(jsonPath("$.data.userRole").value(ROLE.name()))
                .andDo(document("user/update-my-page",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 ID"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.userRole").type(JsonFieldType.STRING).description("유저 권한")
                        )
                ))
        ;
    }
    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    @DisplayName("기능_테스트_User_참여자_기초정보등록_선택지_조회된다")
    public void User_참여자_기초정보등록_선택지_조회() throws Exception {
        // Given
        BaseDataInfoResponseDto responseDto = new BaseDataInfoResponseDto();

        when(userService.getBaseDataInfo()).thenReturn(responseDto);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/surveyee/base-data-info"));

        // Then
        verify(userService, times(1)).getBaseDataInfo();

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.question_1").value(responseDto.getQuestion_1()))
                .andExpect(jsonPath("$.data.question_2").value(responseDto.getQuestion_2()))
                .andExpect(jsonPath("$.data.question_3").value(responseDto.getQuestion_3()))
                .andDo(document("user/get-base-data-info",
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.question_1").type(JsonFieldType.STRING).description("질문"),
                                fieldWithPath("data.question_2").type(JsonFieldType.STRING).description("유저 이메일"),
                                fieldWithPath("data.question_3").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.option_1").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.option_2").type(JsonFieldType.STRING).description("유저 권한"),
                                fieldWithPath("data.option_3").type(JsonFieldType.STRING).description("유저 권한")
                        )
                ))
        ;
    }


    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    @DisplayName("기능_테스트_User_참여자_기초정보_저장")
    public void User_참여자_기초정보_저장() throws Exception {
        // Given
        Long userId = 1L;
        BaseDataRequestDto requestDto = new BaseDataRequestDto();
        ReflectionTestUtils.setField(requestDto, "category", CategoryEnum.AGE);
        ReflectionTestUtils.setField(requestDto, "answer", 2L);
        List<BaseDataRequestDto> listDto = List.of(requestDto);
        BaseDataListRequestDto requestDtoList = new BaseDataListRequestDto();
        ReflectionTestUtils.setField(requestDtoList, "list", listDto);
        doNothing().when(userService).saveBaseDatas(eq(userId), any(BaseDataListRequestDto.class));

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/surveyee/base-datas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDtoList)));

        // Then
//        verify(userService, times(1))
//                .saveBaseDatas(eq(userId), any(BaseDataListRequestDto.class));

        resultActions.andExpect(status().isOk())
                .andDo(document("user/save-base-datas",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        requestFields(
                                fieldWithPath("list[].category").type(JsonFieldType.STRING).description("질문 카테고리"),
                                fieldWithPath("list[].answer").type(JsonFieldType.NUMBER).description("질문 응답")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 Data")

                        )
                ))
        ;
    }
    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    @DisplayName("기능_테스트_User_참여자_기초정보등록_결과_조회")
    public void User_참여자_기초정보등록_결과_조회() throws Exception {
        // Given
        Long userId = 1L;
        BaseDataResponseDto responseDto = new BaseDataResponseDto(CategoryEnum.AGE, 1L);
        BaseDataListResponseDto listResponseDto = new BaseDataListResponseDto(List.of(responseDto));

        when(userService.getBaseDatas(eq(userId))).thenReturn(listResponseDto);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/surveyee/base-datas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}"));

        // Then
        verify(userService, times(1)).getBaseDatas(eq(userId));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].category").value(Matchers.equalTo(responseDto.getCategory().name())))
                .andExpect(jsonPath("$.data.list[0].data").value(responseDto.getData()))
                .andDo(document("user/get-base-datas",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.list[].category").type(JsonFieldType.STRING).description("질문 카테고리"),
                                fieldWithPath("data.list[].data").type(JsonFieldType.NUMBER).description("질문 응답")
                        )
                ))
        ;
    }
}
