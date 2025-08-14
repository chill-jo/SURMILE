package com.example.surveyapp.domain.survey.presentation;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.survey.application.OptionsService;
import com.example.surveyapp.domain.survey.application.QuestionService;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.presentation.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.PageQuestionResponseDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.QuestionResponseDto;
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
@DisplayName("controller : OptionsController 테스트")
@WebMvcTest(controllers = OptionsController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class OptionsControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private OptionsService optionsService;

    @Test
    @DisplayName("기능_선택지를_생성한다.")
    @WithCustomMockUser(id = 2L, role = UserRoleEnum.SURVEYOR)
    void 선택지_생성() throws Exception{
        Long userId = 2L;
        Long surveyId = 1L;
        Long questionId = 1L;

        OptionCreateRequestDto requestDto = new OptionCreateRequestDto(
                1L,
                "테스트선택지내용"
        );

        OptionResponseDto responseDto = new OptionResponseDto(
                1L,
                1L,
                "테스트선택지내용"
        );

        when(optionsService.createOption(eq(userId), eq(surveyId), eq(questionId), any(OptionCreateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/survey/{surveyId}/question/{questionId}/option", surveyId, questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(optionsService, times(1))
                .createOption(eq(userId), eq(surveyId), eq(questionId), any(OptionCreateRequestDto.class));

        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.number").value(requestDto.getNumber()))
                .andExpect(jsonPath("$.data.content").value(requestDto.getContent()));
    }

    @Test
    @DisplayName("기능_선택지목록을_조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    void 참여자가_선택지목록을_조회한다() throws Exception{
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;

        OptionResponseDto responseDto1 = new OptionResponseDto(
                1L,
                1L,
                "테스트선택지내용1"
        );
        OptionResponseDto responseDto2 = new OptionResponseDto(
                2L,
                2L,
                "테스트선택지내용2"
        );

        List<OptionResponseDto> dtoList = List.of(responseDto1, responseDto2);

        when(optionsService.getOptions(userId, surveyId, questionId)).thenReturn(dtoList);

        ResultActions actions = mockMvc.perform(get("/api/survey/{surveyId}/question/{questionId}/option", surveyId, questionId));

        verify(optionsService, times(1))
                .getOptions(userId, surveyId, questionId);

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[0].content").value("테스트선택지내용1"))
                .andExpect(jsonPath("$.data[1].content").value("테스트선택지내용2"));
    }
    @Test
    @DisplayName("기능_선택지를_삭제한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 선택지를_삭제한다() throws Exception{
        Long surveyId = 1L;
        Long questionId = 1L;
        Long optionId = 1L;
        Long userId = 1L;

        doNothing().when(optionsService).deleteOption(eq(userId), eq(surveyId), eq(questionId), eq(optionId));

        ResultActions actions = mockMvc.perform(delete("/api/survey/{surveyId}/question/{questionId}/option/{optionId}", surveyId, questionId, optionId));

        verify(optionsService, times(1))
                .deleteOption(eq(userId), eq(surveyId), eq(questionId), eq(optionId));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
