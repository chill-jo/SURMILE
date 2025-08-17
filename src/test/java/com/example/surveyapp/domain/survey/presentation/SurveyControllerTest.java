package com.example.surveyapp.domain.survey.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.generator.SurveyFixtureGenerator;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.domain.survey.application.SurveyService;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyStatusUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.*;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@DisplayName("controller : SurveyController 테스트")
@Import(TestMockBeans.class)
public class SurveyControllerTest extends WebMvcTestBase {

    @Autowired
    private SurveyService surveyService;

    @Test
    @DisplayName("기능_설문을 생성한다.")
    @WithCustomMockUser(id = 2L, role = UserRoleEnum.SURVEYOR)
    void 설문_생성() throws Exception{

        Long userId = 2L;
        Survey survey = SurveyFixtureGenerator.generateSurveyFixture();
        SurveyInfo surveyInfo = survey.getSurveyInfo();
        SurveyCreateRequestDto requestDto = new SurveyCreateRequestDto(
                surveyInfo.getTitle(),
                surveyInfo.getDescription(),
                surveyInfo.getMaxSurveyee(),
                surveyInfo.getPointPerPerson().getValue(),
                surveyInfo.getDeadline(),
                surveyInfo.getExpectedTime()
        );
        SurveyResponseDto responseDto = new SurveyResponseDto(
                survey.getId(),
                surveyInfo.getTitle(),
                surveyInfo.getDescription(),
                surveyInfo.getMaxSurveyee(),
                surveyInfo.getPointPerPerson().getValue(),
                surveyInfo.getTotalPoint().getValue(),
                surveyInfo.getDeadline(),
                surveyInfo.getExpectedTime(),
                survey.getStatus(),
                null
        );
        when(surveyService.createSurvey(eq(2L), any(SurveyCreateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/survey")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(surveyService, times(1))
                .createSurvey(eq(userId), any(SurveyCreateRequestDto.class));
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.data.status").value(SurveyStatus.NOT_STARTED.name()))
                .andDo(document("create-survey",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("설문 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설문 상세정보"),
                                fieldWithPath("maxSurveyee").type(JsonFieldType.NUMBER).description("설문 참여 가능 인원"),
                                fieldWithPath("pointPerPerson").type(JsonFieldType.NUMBER).description("설문 참여 시 인당 지급 포인트"),
                                fieldWithPath("deadline").type(JsonFieldType.STRING).description("설문 마감 기한"),
                                fieldWithPath("expectedTime").type(JsonFieldType.NUMBER).description("설문 예상 소요 시간")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 설문 위치")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("설문 ID"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("설문 제목"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("설문 상세설명"),
                                fieldWithPath("data.maxSurveyee").type(JsonFieldType.NUMBER).description("설문 참여 가능 인원"),
                                fieldWithPath("data.pointPerPerson").type(JsonFieldType.NUMBER).description("설문 참여 시 인당 지급 포인트"),
                                fieldWithPath("data.totalPoint").type(JsonFieldType.NUMBER).description("설문 참여 마감 시 지급되는 포인트 총액"),
                                fieldWithPath("data.deadline").type(JsonFieldType.STRING).description("설문 마감 기한"),
                                fieldWithPath("data.expectedTime").type(JsonFieldType.NUMBER).description("설문 예상 소요 시간"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("설문 상태"),
                                fieldWithPath("data.surveyeeCount").type(JsonFieldType.NULL).description("설문 참여 인원")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_설문목록을_조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    void 참여자가_설문목록을_조회한다() throws Exception{
        int page = 0;
        int size = 2;

        SurveyResponseDto responseDto1 = new SurveyResponseDto(
                1L,
                "테스트설문제목1",
                "테스트설문내용1",
                10L,
                20L,
                200L,
                LocalDateTime.of(2025, 12, 25, 13, 40, 20),
                20L,
                SurveyStatus.IN_PROGRESS,
                10L
        );
        SurveyResponseDto responseDto2 = new SurveyResponseDto(
                1L,
                "테스트설문제목2",
                "테스트설문내용2",
                10L,
                20L,
                200L,
                LocalDateTime.of(2025, 11, 25, 13, 40, 20),
                20L,
                SurveyStatus.IN_PROGRESS,
                10L
        );


        List<SurveyResponseDto> dtoList = List.of(responseDto1, responseDto2);
        Page<SurveyResponseDto> pageDto = new PageImpl<>(dtoList, PageRequest.of(page, size), dtoList.size());
        PageSurveyResponseDto<SurveyResponseDto> pageSurveyResponseDto =
                new PageSurveyResponseDto<>(pageDto);

        when(surveyService.getSurveys(page, size)).thenReturn(pageSurveyResponseDto);

        ResultActions actions = mockMvc.perform(get("/api/survey")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        verify(surveyService, times(1))
                .getSurveys(page, size);

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("테스트설문제목1"))
                .andExpect(jsonPath("$.data.content[1].title").value("테스트설문제목2"));
    }

    @Test
    @DisplayName("기능_설문상세정보를_수정한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 설문_상세정보를_수정한다() throws Exception{
        Long surveyId = 1L;
        Long userId = 1L;
        SurveyUpdateRequestDto requestDto = new SurveyUpdateRequestDto(
                "테스트설문제목수정",
                "테스트설문내용수정",
                10L,
                20L,
                LocalDateTime.of(2025, 11, 25, 13, 40, 20),
                20L
        );

        SurveyResponseDto responseDto = new SurveyResponseDto(
                1L,
                "테스트설문제목수정",
                "테스트설문내용수정",
                10L,
                20L,
                200L,
                LocalDateTime.of(2025, 11, 25, 13, 40, 20),
                20L,
                SurveyStatus.IN_PROGRESS,
                10L
        );
        when(surveyService.updateSurveyInfo(eq(userId), eq(surveyId), any(SurveyUpdateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(patch("/api/survey/{id}", surveyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(surveyService, times(1))
                .updateSurveyInfo(eq(userId), eq(surveyId), any(SurveyUpdateRequestDto.class));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.data.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.data.maxSurveyee").value(requestDto.getMaxSurveyee()));
    }

    @Test
    @DisplayName("기능_설문상태를_수정한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 설문_상태를_수정한다() throws Exception{
        Long surveyId = 1L;
        Long userId = 1L;
        SurveyStatusUpdateRequestDto requestDto = new SurveyStatusUpdateRequestDto(SurveyStatus.IN_PROGRESS);

        SurveyStatusResponseDto responseDto = new SurveyStatusResponseDto(SurveyStatus.IN_PROGRESS);

        when(surveyService.updateSurveyStatus(eq(userId), eq(surveyId), any(SurveyStatusUpdateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(patch("/api/survey/{id}/status", surveyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(surveyService, times(1))
                .updateSurveyStatus(eq(userId), eq(surveyId), any(SurveyStatusUpdateRequestDto.class));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(requestDto.getStatus().name()));
    }

    @Test
    @DisplayName("기능_설문을_삭제한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 설문을_삭제한다() throws Exception{
        Long surveyId = 1L;
        Long userId = 1L;

        doNothing().when(surveyService).deleteSurvey(eq(userId), eq(surveyId));

        ResultActions actions = mockMvc.perform(delete("/api/survey/{id}", surveyId));

        verify(surveyService, times(1))
                .deleteSurvey(eq(userId), eq(surveyId));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("기능_설문_상세정보를_단건조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 설문_상세정보를_단건조회한다() throws Exception{
        Long surveyId = 1L;

        SurveyResponseDto responseDto = new SurveyResponseDto(
                1L,
                "테스트설문제목",
                "테스트설문내용",
                10L,
                20L,
                200L,
                LocalDateTime.of(2025, 12, 25, 13, 40, 20),
                20L,
                SurveyStatus.IN_PROGRESS,
                10L
        );

        when(surveyService.getSurvey(eq(surveyId))).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/{id}", surveyId));

        verify(surveyService, times(1))
                .getSurvey(eq(surveyId));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("테스트설문제목"))
                .andExpect(jsonPath("$.data.description").value("테스트설문내용"))
                .andExpect(jsonPath("$.data.maxSurveyee").value(responseDto.getMaxSurveyee()));
    }

    @Test
    @DisplayName("기능_설문_시작하면_설문질문선택지를_한번에_조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    void 설문질문선택지를_한번에_조회한다() throws Exception{
        Long surveyId = 1L;
        Long userId = 1L;
        OptionResponseDto optionDto1 = new OptionResponseDto(
                1L,
                1L,
                "테스트선택지내용1"
        );
        OptionResponseDto optionDto2 = new OptionResponseDto(
                2L,
                2L,
                "테스트선택지내용2"
        );
        QuestionOptionsDto questionDto = new QuestionOptionsDto(
                1L,
                1L,
                "테스트질문내용",
                QuestionType.SINGLE_CHOICE,
                List.of(optionDto1, optionDto2)
                );
        SurveyQuestionDto responseDto = new SurveyQuestionDto(
                1L,
                "테스트설문제목",
                "테스트설문내용",
                10L,
                20L,
                200L,
                LocalDateTime.of(2025, 12, 25, 13, 40, 20),
                20L,
                List.of(questionDto)
        );

        when(surveyService.startSurvey(eq(userId), eq(surveyId))).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/{id}/start", surveyId));

        verify(surveyService, times(1))
                .startSurvey(eq(userId), eq(surveyId));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("테스트설문제목"))
                .andExpect(jsonPath("$.data.questions[0].content").value("테스트질문내용"))
                .andExpect(jsonPath("$.data.questions[0].options[0].number").value(1L))
                .andExpect(jsonPath("$.data.questions[0].options[1].number").value(2L))
                .andExpect(jsonPath("$.data.questions[0].options[0].content").value("테스트선택지내용1"))
                .andExpect(jsonPath("$.data.questions[0].options[1].content").value("테스트선택지내용2"));
    }

}
