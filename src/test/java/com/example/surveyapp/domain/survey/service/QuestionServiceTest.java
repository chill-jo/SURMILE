package com.example.surveyapp.domain.survey.service;

import static org.assertj.core.api.Assertions.*;
import com.example.surveyapp.domain.survey.controller.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.PageQuestionResponseDto;
import com.example.surveyapp.domain.survey.controller.dto.response.QuestionResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;

@DisplayName("Service::Question")
@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionsRepository optionsRepository;

    @Mock
    private UserFacade userFacade;

    @Mock
    private SurveyRepository surveyRepository;

    @InjectMocks
    private QuestionService questionService;

    @Captor
    ArgumentCaptor<Question> questionCaptor;

    @Test
    @DisplayName("기능_질문 생성을 성공한다")
    void 질문을_생성한다(){
        Long questionId = 1L;
        Long userId = 1L;
        Long surveyId = 1L;
        Long number = 1L;
        String content = "테스트질문내용";
        QuestionType type = QuestionType.SINGLE_CHOICE;

        User userMock = mock(User.class);
        Survey surveyMock = mock(Survey.class);

        QuestionCreateRequestDto requestDto = new QuestionCreateRequestDto(number, content, type);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);
        when(surveyMock.isNotStarted()).thenReturn(true);

        // question을 생성자로 만들어서 저장하고 있기 때문에
        // fixture를 전달해 줄 수 없어서 fixture generator 사용할 수 없다
        // 생성자로 만들어진 question 객체가 인자로 전달되고, survey, number,content, type 필드는 채워진다.
        // 그렇지만 jpa가 실행되지 않기 때문에 id값은 안생김.
        // 그래서 id 값 직접 세팅
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> {
            Question q = invocation.getArgument(0);
            ReflectionTestUtils.setField(q, "id", 1L);
            return q;
        });

        //when
        QuestionResponseDto responseDto = questionService.createQuestion(userId, surveyId, requestDto);

        //then
        verify(questionRepository).save(questionCaptor.capture());

        Question savedQuestion = questionCaptor.getValue();
        assertThat(savedQuestion.getSurvey()).isSameAs(surveyMock);

        assertThat(responseDto.getId()).isEqualTo(questionId);
        assertThat(responseDto.getNumber()).isEqualTo(number);
        assertThat(responseDto.getContent()).isEqualTo(content);
        assertThat(responseDto.getType()).isEqualTo(type);
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(surveyMock).isNotStarted();
        verify(questionRepository).save(any(Question.class));
    }


    @Test
    @DisplayName("기능_설문 진행전 단건 조회를 성공한다")
    void 질문이_진행중이_아닐때_설문출제자가_단건_조회한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;
        Long number = 1L;
        String content = "테스트질문내용";
        QuestionType type = QuestionType.SINGLE_CHOICE;

        Survey surveyMock = mock(Survey.class);
        User userMock = mock(User.class);
        Question questionMock = mock(Question.class);

        when(userFacade.findUser(userId)).thenReturn(userMock);

        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionMock));

        when(questionMock.isFromSurvey(surveyMock)).thenReturn(true);

        when(surveyMock.isInProgress()).thenReturn(false);
        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);
        when(questionMock.getId()).thenReturn(questionId);
        when(questionMock.getNumber()).thenReturn(number);
        when(questionMock.getContent()).thenReturn(content);
        when(questionMock.getType()).thenReturn(type);

        //when
        QuestionResponseDto responseDto = questionService.getQuestion(userId, surveyId, questionId);

        //then
        assertThat(responseDto.getId()).isEqualTo(questionId);
        assertThat(responseDto.getNumber()).isEqualTo(number);
        assertThat(responseDto.getContent()).isEqualTo(content);
        assertThat(responseDto.getType()).isEqualTo(type);

        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(questionRepository).findById(questionId);
        verify(questionMock).isFromSurvey(surveyMock);
        verify(surveyMock).isInProgress();
        verify(surveyMock).isUserSurveyCreator(userMock);

    }

    @Test
    @DisplayName("기능_질문 목록 조회를 성공한다")
    void 질문_목록을_조회한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;
        Long number = 1L;
        String content = "테스트질문내용";
        QuestionType type = QuestionType.SINGLE_CHOICE;
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);

        Survey surveyMock = mock(Survey.class);
        User userMock = mock(User.class);
        Question questionMock1 = mock(Question.class);
        Question questionMock2 = mock(Question.class);

        List<Question> questionMockList = List.of(questionMock1, questionMock2);
        Page<Question> questionMockPage = new PageImpl<>(questionMockList, pageable, questionMockList.size());
        when(userFacade.findUser(userId)).thenReturn(userMock);

        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(surveyMock.isInProgress()).thenReturn(true);

        when(questionRepository.findAllBySurveyId(surveyId, pageable)).thenReturn(questionMockPage);

        when(questionMock1.getId()).thenReturn(1L);
        when(questionMock1.getNumber()).thenReturn(1L);
        when(questionMock1.getContent()).thenReturn("테스트질문내용1");
        when(questionMock1.getType()).thenReturn(QuestionType.SINGLE_CHOICE);
        when(questionMock2.getId()).thenReturn(2L);
        when(questionMock2.getNumber()).thenReturn(2L);
        when(questionMock2.getContent()).thenReturn("테스트질문내용2");
        when(questionMock2.getType()).thenReturn(QuestionType.SUBJECTIVE);


        //when
        PageQuestionResponseDto<QuestionResponseDto> pageQuestionResponseDto = questionService.getQuestions(page, size, userId, surveyId);

        //then
        assertThat(pageQuestionResponseDto).isNotNull();
        assertThat(pageQuestionResponseDto.getContent()).hasSize(2);
        assertThat(pageQuestionResponseDto.getContent().get(0).getId())
                .isEqualTo(questionMock1.getId());
        assertThat(pageQuestionResponseDto.getContent().get(1).getId())
                .isEqualTo(questionMock2.getId());
        assertThat(pageQuestionResponseDto.getTotalElements()).isEqualTo(questionMockList.size());

        verify(userFacade).findUser(userId);
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(surveyMock).isInProgress();
        verify(questionRepository).findAllBySurveyId(surveyId, pageable);
        verify(questionMock1).getNumber();
        verify(questionMock2).getContent();


    }

    @Test
    @DisplayName("기능_질문 종류 변경을 성공한다")
    void 관리자가_질문을_주관식으로_수정한다(){

        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;
        Long number = 2L;
        String content = "테스트질문내용수정";
        QuestionType type = QuestionType.SUBJECTIVE;

        User userMock = mock(User.class);
        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        ReflectionTestUtils.setField(questionMock, "survey", surveyMock);

        QuestionUpdateRequestDto requestDto = new QuestionUpdateRequestDto(number, content, type);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(false);
        when(userMock.isUserRoleNotAdmin()).thenReturn(false);
        when(surveyMock.isNotStarted()).thenReturn(true);
        when(questionMock.isSubjective()).thenReturn(true);
        when(questionMock.isFromSurvey(surveyMock)).thenReturn(true);

        when(questionMock.getId()).thenReturn(questionId);
        when(questionMock.getNumber()).thenReturn(number);
        when(questionMock.getContent()).thenReturn(content);
        when(questionMock.getType()).thenReturn(type);

        //when
        QuestionResponseDto responseDto = questionService.updateQuestion(userId, surveyId, questionId, requestDto);

        //then
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(questionRepository).findById(questionId);
        verify(userMock).isUserRoleNotAdmin();
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(surveyMock).isNotStarted();
        verify(questionMock).changeNumber(number);
        verify(questionMock).changeContent(content);
        verify(questionMock).changeQuestionType(type);
        verify(optionsRepository).deleteAllByQuestion(questionMock);

        assertThat(responseDto.getNumber()).isEqualTo(number);
        assertThat(responseDto.getContent()).isEqualTo(content);
        assertThat(responseDto.getType()).isEqualTo(questionMock.getType());

    }

    @Test
    @DisplayName("기능_질문 삭제를 성공한다")
    void 해당설문_출제자가_질문을_삭제한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;

        User userMock = mock(User.class);
        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        ReflectionTestUtils.setField(questionMock, "survey", surveyMock);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(surveyMock));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);
        when(surveyMock.isNotStarted()).thenReturn(true);
        when(questionMock.isFromSurvey(surveyMock)).thenReturn(true);

        doNothing().when(questionRepository).delete(questionMock);

        //when
        questionService.deleteQuestion(userId, surveyId, questionId);

        //then
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(questionRepository).findById(questionId);
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(surveyMock).isNotStarted();
        verify(optionsRepository).deleteAllByQuestion(questionMock);
        verify(questionRepository).delete(questionMock);
    }
}
