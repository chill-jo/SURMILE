package com.example.surveyapp.domain.survey.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.surveyapp.domain.ai.moderation.application.facade.AiModerationFacade;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.domain.survey.presentation.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.OptionUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.global.reader.UserReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@DisplayName("Service::Options")
@ExtendWith(MockitoExtension.class)
public class OptionsServiceTest{

    @Mock
    private SurveyQueryService surveyQueryService;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private OptionsService optionsService;

    @Mock
    private AiModerationFacade aiModerationFacade;

    @Test
    @DisplayName("기능_선택지 생성을 성공한다")
    void 선택지를_생성한다(){
        // given
        Long userId = 1L;
        Long surveyId = 2L;
        Long questionId = 3L;

        OptionCreateRequestDto requestDto = new OptionCreateRequestDto(10L, "테스트선택지내용");
        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(optionsService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(optionsService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        Options option = new Options(requestDto.getNumber(), requestDto.getContent());
        ReflectionTestUtils.setField(option, "id", 1L);

        doNothing().when(userReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        when(surveyQuestionService.getQuestionById(surveyMock, questionId)).thenReturn(questionMock);
        doNothing().when(surveyValidator).validateUpdatable(userId, surveyMock);
        when(aiModerationFacade.checkOptionsModeration(eq(userId), eq("테스트선택지내용")))
                .thenReturn(AiModerationResult.of(null, AiModerationResultStatusEnum.APPROVED));

        // when
        OptionResponseDto responseDto = optionsService.createOption(userId, surveyId, questionId, requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getNumber()).isEqualTo(requestDto.getNumber());
        assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());

        verify(userReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyQuestionService).getQuestionById(surveyMock, questionId);
        verify(surveyValidator).validateUpdatable(userId, surveyMock);
        verify(questionMock).addOption(any(Options.class));
    }

    @Test
    @DisplayName("기능_선택지 목록 조회를 성공한다")
    void 선택지_목록을_조회한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;

        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(optionsService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(optionsService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        Options optionMock1 = mock(Options.class);
        Options optionMock2 = mock(Options.class);

        List<Options> optionsMockList = List.of(optionMock1, optionMock2);

        doNothing().when(userReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        when(surveyQuestionService.getQuestionById(surveyMock, questionId)).thenReturn(questionMock);
        when(questionMock.getOptions()).thenReturn(optionsMockList);

        when(optionMock1.getId()).thenReturn(1L);
        when(optionMock1.getNumber()).thenReturn(1L);
        when(optionMock1.getContent()).thenReturn("테스트질문지1번");

        when(optionMock2.getId()).thenReturn(2L);
        when(optionMock2.getNumber()).thenReturn(2L);
        when(optionMock2.getContent()).thenReturn("테스트질문지2번");

        //when
        List<OptionResponseDto> responseDtoList = optionsService.getOptions(userId, surveyId, questionId);

        //then
        verify(userReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyQuestionService).getQuestionById(surveyMock, questionId);
        verify(userReader).validateUserRoleToSurveyee(userId);
        verify(surveyValidator).validateQuestionAccess(userId, surveyMock, false);
        verify(questionMock).getOptions();

        assertThat(responseDtoList.size()).isEqualTo(2);
        assertThat(responseDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(responseDtoList.get(1).getId()).isEqualTo(2L);
        assertThat(responseDtoList.get(0).getContent()).isEqualTo("테스트질문지1번");
        assertThat(responseDtoList.get(1).getContent()).isEqualTo("테스트질문지2번");
    }

    @Test
    @DisplayName("기능_선택지 내용 수정을 성공한다")
    void 선택지를_수정한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;
        Long optionId = 1L;

        Long number = 2L;
        String content = "테스트질문지내용수정";

        OptionUpdateRequestDto requestDto = new OptionUpdateRequestDto(number, content);
        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(optionsService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(optionsService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        Options optionMock = mock(Options.class);

        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        when(surveyQuestionService.getQuestionById(surveyMock, questionId)).thenReturn(questionMock);
        doNothing().when(surveyValidator).validateUpdatable(userId, surveyMock);
        when(questionMock.updateOption(optionId, requestDto.getNumber(), requestDto.getContent())).thenReturn(optionMock);
        when(aiModerationFacade.checkOptionsModeration(eq(userId), eq("테스트질문지내용수정")))
                .thenReturn(AiModerationResult.of(null, AiModerationResultStatusEnum.APPROVED));

        when(optionMock.getId()).thenReturn(optionId);
        when(optionMock.getNumber()).thenReturn(number);
        when(optionMock.getContent()).thenReturn(content);

        //when
        OptionResponseDto responseDto = optionsService.updateOption(userId, surveyId, questionId, optionId, requestDto);

        //then
        assertThat(responseDto.getId()).isEqualTo(optionId);
        assertThat(responseDto.getNumber()).isEqualTo(requestDto.getNumber());
        assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());

        verify(userReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyQuestionService).getQuestionById(surveyMock, questionId);
        verify(surveyValidator).validateUpdatable(userId, surveyMock);
        verify(questionMock).updateOption(optionId, requestDto.getNumber(), requestDto.getContent());
        verify(optionMock).getId();
        verify(optionMock).getNumber();
        verify(optionMock).getContent();
    }

    @Test
    @DisplayName("기능_선택지 삭제를 성공한다")
    void 선택지를_삭제한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;
        Long optionId = 1L;
        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(optionsService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(optionsService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);

        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        when(surveyQuestionService.getQuestionById(surveyMock, questionId)).thenReturn(questionMock);

        // when
        optionsService.deleteOption(userId, surveyId, questionId, optionId);

        // then
        verify(userReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyQuestionService).getQuestionById(surveyMock, questionId);
        verify(surveyValidator).validateDeletable(userId, surveyMock);
        verify(questionMock).deleteOptionById(optionId);
    }
}

