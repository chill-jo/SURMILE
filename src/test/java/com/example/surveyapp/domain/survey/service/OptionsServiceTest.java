package com.example.surveyapp.domain.survey.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.surveyapp.domain.survey.controller.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.OptionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.OptionsRepository;
import com.example.surveyapp.domain.survey.domain.repository.QuestionRepository;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.facade.UserFacade;
import com.example.surveyapp.domain.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

@DisplayName("Service::Options")
@ExtendWith(MockitoExtension.class)
public class OptionsServiceTest {

    @Mock
    private OptionsRepository optionsRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private OptionsService optionsService;

    @Captor
    ArgumentCaptor<Options> optionsCaptor;

    @Test
    @DisplayName("기능_선택지 생성을 성공한다")
    void 선택지를_생성한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;
        Long number = 1L;
        String content = "테스트선택지내용";

        User userMock = mock(User.class);
        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);

        OptionCreateRequestDto requestDto = new OptionCreateRequestDto(number, content);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionMock));

        when(questionMock.isSubjective()).thenReturn(false);

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);
        when(surveyMock.isNotStarted()).thenReturn(true);
        when(questionMock.isFromSurvey(surveyId)).thenReturn(true);

        when(optionsRepository.save(any(Options.class))).thenAnswer(invocation -> {
            Options o = invocation.getArgument(0);
            ReflectionTestUtils.setField(o, "id", 1L);
            return o;
        });

        //when
        OptionResponseDto responseDto = optionsService.createOption(userId, surveyId, questionId, requestDto);

        //then

        verify(optionsRepository).save(optionsCaptor.capture());
        Options savedOption = optionsCaptor.getValue();
        assertThat(savedOption.getQuestionId()).isSameAs(questionId);
        assertThat(responseDto.getNumber()).isEqualTo(number);
        assertThat(responseDto.getContent()).isEqualTo(content);

        verify(userFacade).findUser(userId);
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(questionRepository).findById(questionId);
        verify(questionMock).isSubjective();
    }

    @Test
    @DisplayName("기능_선택지 목록 조회를 성공한다")
    void 선택지_목록을_조회한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;

        User userMock = mock(User.class);
        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        Options optionMock1 = mock(Options.class);
        Options optionMock2 = mock(Options.class);

        List<Options> optionsMockList = List.of(optionMock1, optionMock2);

        when(userFacade.findUser(userId)).thenReturn(userMock);

        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionMock));

        when(surveyMock.isInProgress()).thenReturn(false);
        when(userMock.isUserRoleSurveyee()).thenReturn(false);
        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);

        when(optionsRepository.findAllByQuestionId(questionId)).thenReturn(optionsMockList);

        when(optionMock1.getId()).thenReturn(1L);
        when(optionMock1.getNumber()).thenReturn(1L);
        when(optionMock1.getContent()).thenReturn("테스트질문지1번");

        when(optionMock2.getId()).thenReturn(2L);
        when(optionMock2.getNumber()).thenReturn(2L);
        when(optionMock2.getContent()).thenReturn("테스트질문지2번");

        //when
        List<OptionResponseDto> responseDtoList = optionsService.getOptions(userId, surveyId, questionId);

        //then
        verify(userFacade).findUser(userId);
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(questionRepository).findById(questionId);
        verify(surveyMock).isInProgress();
        verify(userMock).isUserRoleSurveyee();
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(optionsRepository).findAllByQuestionId(questionId);

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

        User userMock = mock(User.class);
        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        Options optionMock = mock(Options.class);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionMock));
        when(optionsRepository.findById(optionId)).thenReturn(Optional.of(optionMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);
        when(surveyMock.isNotStarted()).thenReturn(true);
        when(questionMock.isFromSurvey(surveyId)).thenReturn(true);
        when(optionMock.isFromQuestion(questionId)).thenReturn(true);

        doNothing().when(optionMock).changeNumber(number);
        doNothing().when(optionMock).changeContent(content);

        when(optionMock.getId()).thenReturn(optionId);
        when(optionMock.getNumber()).thenReturn(number);
        when(optionMock.getContent()).thenReturn(content);

        //when
        OptionResponseDto responseDto = optionsService.updateOption(userId, surveyId, questionId, optionId, requestDto);

        //then
        assertThat(responseDto.getId()).isEqualTo(optionId);
        assertThat(responseDto.getNumber()).isEqualTo(number);
        assertThat(responseDto.getContent()).isEqualTo(content);

        verify(userFacade).findUser(userId);
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(questionRepository).findById(questionId);
        verify(optionsRepository).findById(optionId);
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(surveyMock).isNotStarted();
        verify(questionMock).isFromSurvey(surveyId);
        verify(optionMock).isFromQuestion(questionId);
        verify(optionMock).changeNumber(number);
        verify(optionMock).changeContent(content);
    }

    @Test
    @DisplayName("기능_선택지 삭제를 성공한다")
    void 선택지를_삭제한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;
        Long optionId = 1L;

        User userMock = mock(User.class);
        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        Options optionMock = mock(Options.class);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionMock));
        when(optionsRepository.findById(optionId)).thenReturn(Optional.of(optionMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);
        when(surveyMock.isNotStarted()).thenReturn(true);
        when(questionMock.isFromSurvey(surveyId)).thenReturn(true);
        when(optionMock.isFromQuestion(questionId)).thenReturn(true);

        doNothing().when(optionsRepository).delete(optionMock);

        //when
        optionsService.deleteOption(userId, surveyId, questionId, optionId);

        //then
        verify(userFacade).findUser(userId);
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(questionRepository).findById(questionId);
        verify(optionsRepository).findById(optionId);
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(surveyMock).isNotStarted();
        verify(questionMock).isFromSurvey(surveyId);
        verify(optionsRepository).delete(optionMock);
    }
}
