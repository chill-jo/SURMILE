package com.example.surveyapp.domain.surveyanswer.presentation;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.survey.application.OptionsService;
import com.example.surveyapp.domain.survey.presentation.SurveyController;
import com.example.surveyapp.domain.survey.presentation.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.surveyanswer.application.SurveyAnswerService;
import com.example.surveyapp.domain.surveyanswer.application.SurveyAnswerStatisticsService;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.request.SurveyAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.*;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.surveyapp.config.generator.QuestionFixtureGenerator.surveyId;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@DisplayName("controller : SurveyAnswerController 테스트")
@WebMvcTest(controllers = SurveyAnswerController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class SurveyAnswerControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private SurveyAnswerService surveyAnswerService;

    @MockitoBean
    private SurveyAnswerStatisticsService surveyAnswerStatisticsService;

    @Test
    @DisplayName("기능_설문응답을_생성한다.")
    @WithCustomMockUser(id = 2L, role = UserRoleEnum.SURVEYEE)
    void 설문응답_생성() throws Exception{
        Long userId = 2L;
        Long surveyId = 1L;

        QuestionAnswerRequestDto questionAnswerRequestDto1 = new QuestionAnswerRequestDto(
                1L,
                1
        );
        QuestionAnswerRequestDto questionAnswerRequestDto2 = new QuestionAnswerRequestDto(
                1L,
                "테스트주관식응답"
        );

        SurveyAnswerRequestDto requestDto = new SurveyAnswerRequestDto(
                List.of(questionAnswerRequestDto1, questionAnswerRequestDto2)
        );

        doNothing().when(surveyAnswerService).saveSurveyAnswer(eq(surveyId), any(SurveyAnswerRequestDto.class), eq(userId));

        ResultActions actions = mockMvc.perform(post("/api/survey/{surveyId}/answer", surveyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(surveyAnswerService, times(1))
                .saveSurveyAnswer(eq(surveyId), any(SurveyAnswerRequestDto.class), eq(userId));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("기능_설문참여목록을_조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    void 참여자가_설문참여목록을_조회한다() throws Exception{
        Long userId = 1L;

        SurveyeeSurveyDto responseDto1 = new SurveyeeSurveyDto(
                1L,
                "테스트참여설문제목1",
                LocalDateTime.of(2025, 8, 14, 12, 30, 30)
        );
        SurveyeeSurveyDto responseDto2 = new SurveyeeSurveyDto(
                2L,
                "테스트참여설문제목2",
                LocalDateTime.of(2025, 9, 14, 12, 30, 30)
        );

        SurveyeeSurveyListDto surveyListDto = new SurveyeeSurveyListDto();
        surveyListDto.addSurveyeeSurveyDto(responseDto1);
        surveyListDto.addSurveyeeSurveyDto(responseDto2);

        when(surveyAnswerService.getUserSurveyAnswerHistory(userId)).thenReturn(surveyListDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/surveyee"));

        verify(surveyAnswerService, times(1))
                .getUserSurveyAnswerHistory(userId);

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.survey[0].surveyId").value(1L))
                .andExpect(jsonPath("$.data.survey[1].surveyId").value(2L))
                .andExpect(jsonPath("$.data.survey[0].title").value("테스트참여설문제목1"))
                .andExpect(jsonPath("$.data.survey[1].title").value("테스트참여설문제목2"));
    }

    @Test
    @DisplayName("기능_설문통계를_조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 참여자가_설문통계를_조회한다() throws Exception{
        Long surveyId = 1L;

        SurveyStatisticsOptionsDto optionsDto1 = new SurveyStatisticsOptionsDto(
                1L,
                1L,
                "테스트선택지내용1",
                10L
        );
        SurveyStatisticsOptionsDto optionsDto2 = new SurveyStatisticsOptionsDto(
                2L,
                2L,
                "테스트선택지내용2",
                15L
        );

        SurveyStatisticsQuestionDto questionDto1 = new SurveyStatisticsQuestionDto(
                1L,
                1L,
                "테스트질문내용1"
        );
        SurveyStatisticsQuestionDto questionDto2 = new SurveyStatisticsQuestionDto(
                2L,
                2L,
                "테스트질문내용2"
        );
        questionDto1.addOptionsDtoList(List.of(optionsDto1, optionsDto2));

        SurveyStatisticsDto surveyStatisticsDto = new SurveyStatisticsDto(
                1L,
                "테스트설문제목",
                "테스트설문내용",
                50L,
                100L,
                5000L,
                LocalDateTime.of(2025, 12, 25, 12, 30, 30),
                30L,
                49L
        );
        surveyStatisticsDto.addQuestionDtoList(List.of(questionDto1, questionDto2));

        when(surveyAnswerStatisticsService.getStatistics(surveyId)).thenReturn(surveyStatisticsDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/{surveyId}/result", surveyId));

        verify(surveyAnswerStatisticsService, times(1))
                .getStatistics(surveyId);

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.surveyId").value(1L))
                .andExpect(jsonPath("$.data.questions[0].number").value(1L))
                .andExpect(jsonPath("$.data.questions[1].number").value(2L))
                .andExpect(jsonPath("$.data.questions[0].options[0].content").value("테스트선택지내용1"))
                .andExpect(jsonPath("$.data.questions[0].options[1].content").value("테스트선택지내용2"))
                .andExpect(jsonPath("$.data.questions[1].options").isEmpty());

    }
}
