package com.example.surveyapp.domain.survey.presentation;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.generator.SurveyFixtureGenerator;
import com.example.surveyapp.domain.survey.application.QuestionService;
import com.example.surveyapp.domain.survey.application.SurveyService;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyStatusUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.*;
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

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("controller : QuestionController 테스트")
@WebMvcTest(controllers = QuestionController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class QuestionControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    @Test
    @DisplayName("기능_질문을 생성한다.")
    @WithCustomMockUser(id = 2L, role = UserRoleEnum.SURVEYOR)
    void 질문_생성() throws Exception{

        Long userId = 2L;
        Long surveyId = 1L;

        QuestionCreateRequestDto requestDto = new QuestionCreateRequestDto(
                1L,
                "테스트질문내용",
                QuestionType.SINGLE_CHOICE
        );

        QuestionResponseDto responseDto = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용",
                QuestionType.SINGLE_CHOICE
        );

        when(questionService.createQuestion(eq(userId), eq(surveyId), any(QuestionCreateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/survey/{surveyId}", surveyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(questionService, times(1))
                .createQuestion(eq(userId), eq(surveyId), any(QuestionCreateRequestDto.class));

        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.number").value(requestDto.getNumber()))
                .andExpect(jsonPath("$.data.content").value(requestDto.getContent()));
    }

    @Test
    @DisplayName("기능_질문목록을_조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    void 참여자가_질문목록을_조회한다() throws Exception{
        int page = 0;
        int size = 2;
        Long userId = 1L;
        Long surveyId = 1L;

        QuestionResponseDto responseDto1 = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용1",
                QuestionType.SINGLE_CHOICE
        );
        QuestionResponseDto responseDto2 = new QuestionResponseDto(
                1L,
                2L,
                "테스트질문내용2",
                QuestionType.SUBJECTIVE
        );
        List<QuestionResponseDto> dtoList = List.of(responseDto1, responseDto2);
        Page<QuestionResponseDto> pageDto = new PageImpl<>(dtoList, PageRequest.of(page, size), dtoList.size());
        PageQuestionResponseDto<QuestionResponseDto> pageQuestionResponseDto =
                new PageQuestionResponseDto<>(pageDto);

        when(questionService.getQuestions(page, size, userId, surveyId)).thenReturn(pageQuestionResponseDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/{surveyId}/question", surveyId)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        verify(questionService, times(1))
                .getQuestions(page, size, userId, surveyId);

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].content").value("테스트질문내용1"))
                .andExpect(jsonPath("$.data.content[1].content").value("테스트질문내용2"));
    }

    @Test
    @DisplayName("기능_질문_단건조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 질문_단건조회한다() throws Exception{
        Long surveyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;

        QuestionResponseDto responseDto = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용",
                QuestionType.SINGLE_CHOICE
        );

        when(questionService.getQuestion(eq(userId), eq(surveyId), eq(questionId))).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/{surveyId}/question/{questionId}", surveyId, questionId));

        verify(questionService, times(1))
                .getQuestion(eq(userId), eq(surveyId), eq(questionId));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("테스트질문내용"))
                .andExpect(jsonPath("$.data.number").value(responseDto.getNumber()))
                .andExpect(jsonPath("$.data.type").value(responseDto.getType().name()));
    }

    @Test
    @DisplayName("기능_질문_상세정보를_수정한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 질문_상세정보를_수정한다() throws Exception{
        Long surveyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;
        QuestionUpdateRequestDto requestDto = new QuestionUpdateRequestDto(
                null,
                "테스트질문내용수정",
                null
        );

        QuestionResponseDto responseDto = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용수정",
                QuestionType.SINGLE_CHOICE
        );

        when(questionService.updateQuestion(eq(userId), eq(surveyId), eq(questionId), any(QuestionUpdateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(patch("/api/survey/{surveyId}/question/{questionId}", surveyId, questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(questionService, times(1))
                .updateQuestion(eq(userId), eq(surveyId), eq(questionId), any(QuestionUpdateRequestDto.class));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value(requestDto.getContent()));
    }

    @Test
    @DisplayName("기능_질문을_삭제한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 질문을_삭제한다() throws Exception{
        Long surveyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;

        doNothing().when(questionService).deleteQuestion(eq(userId), eq(surveyId), eq(questionId));

        ResultActions actions = mockMvc.perform(delete("/api/survey/{surveyId}/question/{questionId}", surveyId, questionId));

        verify(questionService, times(1))
                .deleteQuestion(eq(userId), eq(surveyId), eq(questionId));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
