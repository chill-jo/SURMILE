package com.example.surveyapp.domain.survey.application;

import static org.assertj.core.api.Assertions.*;

import com.example.surveyapp.domain.ai.moderation.application.facade.AiModerationFacade;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.repository.QuestionRepository;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.domain.survey.infrastructure.QuestionReadEntity;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.PageQuestionResponseDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.QuestionResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.global.oauth.reader.OauthReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("Service::Question")
@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionReadRepository;

    @Mock
    private SurveyQueryService surveyQueryService;

    @Mock
    private OauthReader oauthReader;

    @InjectMocks
    private QuestionService questionService;

    @Mock
    private AiModerationFacade aiModerationFacade;

    @Test
    @DisplayName("기능_질문 생성을 성공한다")
    void 질문을_생성한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        Long number = 1L;
        String content = "테스트질문내용";
        QuestionType type = QuestionType.SINGLE_CHOICE;

        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(questionService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(questionService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        QuestionCreateRequestDto requestDto = new QuestionCreateRequestDto(number, content, type);

        when(surveyQueryService.findSurvey(anyLong())).thenReturn(surveyMock);
        doNothing().when(surveyValidator).validateUpdatable(userId, surveyMock);
        when(aiModerationFacade.checkQuestionModeration(eq("테스트질문내용")))
                .thenReturn(AiModerationResult.of(null, AiModerationResultStatusEnum.APPROVED));
        Question question = Question.from(requestDto, surveyId);

        doNothing().when(surveyQuestionService).addQuestion(any(Survey.class), any(Question.class));

        //when
        QuestionResponseDto responseDto = questionService.createQuestion(userId, surveyId, requestDto);

        //then
        verify(oauthReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyValidator).validateUpdatable(userId, surveyMock);
        verify(surveyQuestionService).addQuestion(any(Survey.class), any(Question.class));

        assertNotNull(responseDto);
        assertThat(requestDto.getNumber()).isEqualTo(responseDto.getNumber());
        assertThat(requestDto.getContent()).isEqualTo(responseDto.getContent());
        assertThat(requestDto.getType()).isEqualTo(responseDto.getType());
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

        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(questionService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(questionService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);

        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        when(oauthReader.validateUserRoleToSurveyee(userId)).thenReturn(true);
        when(surveyQuestionService.getQuestionById(surveyMock, questionId)).thenReturn(questionMock);

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

        verify(oauthReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(oauthReader).validateUserRoleToSurveyee(userId);
        verify(surveyValidator).validateQuestionAccess(userId, surveyMock, true);
        verify(surveyQuestionService).getQuestionById(surveyMock, questionId);

    }

    @Test
    @DisplayName("기능_질문 목록 조회를 성공한다")
    void 질문_목록을_조회한다(){
        Long userId = 1L;
        Long surveyId = 1L;
        int page = 0;
        int size = 2;

        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(questionService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(questionService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        Question questionMock1 = mock(Question.class);
        Question questionMock2 = mock(Question.class);
        QuestionReadEntity questionReadEntityMock1 = mock(QuestionReadEntity.class);
        QuestionReadEntity questionReadEntityMock2 = mock(QuestionReadEntity.class);

        doNothing().when(oauthReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionReadEntity> questionReadEntityMockPage = new PageImpl<>(
                List.of(questionReadEntityMock1, questionReadEntityMock2),
                PageRequest.of(page, size),
                2);
        List<Question> questionMockList = List.of(questionMock1, questionMock2);

        when(questionReadRepository.findAllBySurveyId(anyLong(), any(Pageable.class))).thenReturn(questionReadEntityMockPage);
        when(questionReadEntityMock1.toQuestion()).thenReturn(questionMock1);
        when(questionReadEntityMock2.toQuestion()).thenReturn(questionMock2);


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

        verify(oauthReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(oauthReader).validateUserRoleToSurveyee(userId);
        verify(surveyValidator).validateQuestionAccess(userId, surveyMock, false);
        verify(questionReadRepository).findAllBySurveyId(surveyId, pageable);
        verify(questionReadEntityMock1).toQuestion();
        verify(questionReadEntityMock2).toQuestion();
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

        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(questionService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(questionService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        Question questionMock = mock(Question.class);
        QuestionUpdateRequestDto requestDto = new QuestionUpdateRequestDto(number, content, type);

        doNothing().when(oauthReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        doNothing().when(surveyValidator).validateUpdatable(userId, surveyMock);

        when(surveyQuestionService.updateQuestion(
                surveyMock,
                questionId,
                requestDto.getNumber(),
                requestDto.getContent(),
                requestDto.getType()
        )).thenReturn(questionMock);

        when(questionMock.getId()).thenReturn(questionId);
        when(questionMock.getNumber()).thenReturn(number);
        when(questionMock.getContent()).thenReturn(content);
        when(questionMock.getType()).thenReturn(type);

        //when
        QuestionResponseDto responseDto = questionService.updateQuestion(userId, surveyId, questionId, requestDto);

        //then
        verify(oauthReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyValidator).validateUpdatable(userId, surveyMock);
        verify(surveyQuestionService).updateQuestion(
                surveyMock,
                questionId,
                requestDto.getNumber(),
                requestDto.getContent(),
                requestDto.getType()
        );

        assertThat(responseDto.getNumber()).isEqualTo(number);
        assertThat(responseDto.getContent()).isEqualTo(content);
        assertThat(responseDto.getType()).isEqualTo(questionMock.getType());

    }

    @Test
    @DisplayName("기능_질문 삭제를 성공한다")
    void 해당설문_출제자가_질문을_삭제한다() {
        // given
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;

        SurveyValidator surveyValidator = mock(SurveyValidator.class);
        SurveyQuestionService surveyQuestionService = mock(SurveyQuestionService.class);
        ReflectionTestUtils.setField(questionService, "surveyQuestionService", surveyQuestionService);
        ReflectionTestUtils.setField(questionService, "surveyValidator", surveyValidator);

        Survey surveyMock = mock(Survey.class);
        doNothing().when(oauthReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        doNothing().when(surveyValidator).validateDeletable(userId, surveyMock);
        doNothing().when(surveyQuestionService).deleteQuestionById(surveyMock, questionId);

        // when
        questionService.deleteQuestion(userId, surveyId, questionId);

        // then
        verify(oauthReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyValidator).validateDeletable(userId, surveyMock);
        verify(surveyQuestionService).deleteQuestionById(surveyMock, questionId);
    }
}
