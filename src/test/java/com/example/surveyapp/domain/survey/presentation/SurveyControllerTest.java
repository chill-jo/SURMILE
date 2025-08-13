package com.example.surveyapp.domain.survey.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.generator.SurveyFixtureGenerator;
import com.example.surveyapp.domain.survey.application.SurveyService;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@DisplayName("controller : SurveyController 테스트")
@WebMvcTest(controllers = SurveyController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class SurveyControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
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
        when(surveyService.createSurvey(eq(1L), any(SurveyCreateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/survey")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(surveyService, times(1))
                .createSurvey(eq(userId), any(SurveyCreateRequestDto.class));
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(requestDto.getTitle()));

    }

}
