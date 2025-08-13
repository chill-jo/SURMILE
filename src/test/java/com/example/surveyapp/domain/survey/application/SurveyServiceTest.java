package com.example.surveyapp.domain.survey.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.surveyapp.domain.survey.application.facade.SurveyAnswerFacade;
import com.example.surveyapp.domain.survey.application.mapper.SurveyMapper;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.presentation.dto.request.*;
import com.example.surveyapp.domain.survey.presentation.dto.response.*;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyQuestionDto;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.reader.UserReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import com.example.surveyapp.config.generator.SurveyFixtureGenerator;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.user.domain.model.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("Service::Survey")
@ExtendWith(MockitoExtension.class)
public class SurveyServiceTest {
    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private SurveyMapper surveyMapper;

    @Mock
    private UserReader userReader;

    @Mock
    private SurveyQueryService surveyQueryService;

    @Mock
    private SurveyAnswerFacade surveyAnswerFacade;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    @DisplayName("기능_설문 생성을 성공한다")
    void 설문을_생성한다(){
        //Given
        Long userId = 1L;
        SurveyCreateRequestDto requestDto = mock(SurveyCreateRequestDto.class);
        SurveyInfo surveyInfo = mock(SurveyInfo.class);
        Survey savedSurvey = mock(Survey.class);
        SurveyResponseDto responseDto = mock(SurveyResponseDto.class);

        doNothing().when(userReader).validateUserIdOrThrow(userId);
        when(surveyMapper.toSurveyInfo(requestDto)).thenReturn(surveyInfo);
        when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
        when(userReader.validateUserRole(userId, UserRoleEnum.SURVEYOR)).thenReturn(true);
        doNothing().when(eventPublisher).publishEvent(any(SurveyCreateEvent.class));
        when(surveyMapper.toResponseDto(savedSurvey)).thenReturn(responseDto);

        // when
        SurveyResponseDto result = surveyService.createSurvey(userId, requestDto);

        // then
        verify(userReader).validateUserIdOrThrow(userId);
        verify(surveyMapper).toSurveyInfo(requestDto);
        verify(surveyRepository).save(any(Survey.class));
        verify(userReader).validateUserRole(userId, UserRoleEnum.SURVEYOR);
        verify(eventPublisher).publishEvent(any(SurveyCreateEvent.class));
        verify(surveyMapper).toResponseDto(savedSurvey);

        assertThat(responseDto).isEqualTo(result);
    }

    @Test
    @DisplayName("기능_설문 목록 조회를 성공한다")
    void 설문_목록을_조회한다(){
        //Given
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);

        Survey surveyMock1 = SurveyFixtureGenerator.generateSurveyFixture();
        Survey surveyMock2 = SurveyFixtureGenerator.generateSurveyFixture();
        ReflectionTestUtils.setField(surveyMock2, "id", 2L);

        List<Survey> surveyList = List.of(surveyMock1, surveyMock2);
        Page<Survey> surveyPage = new PageImpl<>(surveyList, pageable, surveyList.size());

        when(surveyRepository.findAllSurveyPaged(pageable)).thenReturn(surveyPage);

        when(surveyMapper.toResponseDto(surveyMock1))
                .thenReturn(new SurveyResponseDto(
                        surveyMock1.getId(),
                        surveyMock1.getSurveyInfo().getTitle(),
                        surveyMock1.getSurveyInfo().getDescription(),
                        surveyMock1.getSurveyInfo().getMaxSurveyee(),
                        surveyMock1.getSurveyInfo().getPointPerPerson().getValue(),
                        surveyMock1.getSurveyInfo().getTotalPoint().getValue(),
                        surveyMock1.getSurveyInfo().getDeadline(),
                        surveyMock1.getSurveyInfo().getExpectedTime(),
                        surveyMock1.getStatus(),
                        0L
        ));
        when(surveyMapper.toResponseDto(surveyMock2))
                .thenReturn(new SurveyResponseDto(
                        surveyMock2.getId(),
                        surveyMock2.getSurveyInfo().getTitle(),
                        surveyMock2.getSurveyInfo().getDescription(),
                        surveyMock2.getSurveyInfo().getMaxSurveyee(),
                        surveyMock2.getSurveyInfo().getPointPerPerson().getValue(),
                        surveyMock2.getSurveyInfo().getTotalPoint().getValue(),
                        surveyMock2.getSurveyInfo().getDeadline(),
                        surveyMock2.getSurveyInfo().getExpectedTime(),
                        surveyMock2.getStatus(),
                        0L
                ));

        //When

        PageSurveyResponseDto<SurveyResponseDto> pageResponseDto = surveyService.getSurveys(page, size);

        //Then

        assertThat(pageResponseDto).isNotNull();
        assertThat(pageResponseDto.getContent()).hasSize(2);

        assertThat(pageResponseDto.getContent().get(0).getId()).isEqualTo(surveyMock1.getId());
        assertThat(pageResponseDto.getContent().get(1).getId()).isEqualTo(surveyMock2.getId());
        assertThat(pageResponseDto.getTotalElements()).isEqualTo(surveyList.size());

        verify(surveyRepository, times(1))
                .findAllSurveyPaged(pageable);
        verify(surveyMapper, times(1)).toResponseDto(surveyMock1);
        verify(surveyMapper, times(1)).toResponseDto(surveyMock2);

    }

    @Test
    @DisplayName("기능_설문 상세정보 수정을 성공한다")
    void 설문_상세정보를_수정한다(){
        //given
        User userMock = mock(User.class);
        Long userId = 1L;
        Long surveyId = 1L;
        Long id = 1L;
        String title = "테스트설문제목수정";
        String description = "테스트설문내용수정";
        Long maxSurveyee = 100L;
        Long pointPerPerson = 100L;
        LocalDateTime deadline = LocalDateTime.of(2025, 7,25, 15,30);
        Long expectedTime = 20L;

        SurveyUpdateRequestDto updateRequestDto = new SurveyUpdateRequestDto(
                title, description, maxSurveyee, pointPerPerson, deadline, expectedTime);

        Survey surveyMock = mock(Survey.class);
        SurveyInfo surveyInfoMock = mock(SurveyInfo.class);

        doNothing().when(userReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(surveyId)).thenReturn(surveyMock);
        when(surveyMock.isNotStarted()).thenReturn(true);
        when(surveyMock.isUserSurveyCreator(anyLong())).thenReturn(true);
        when(surveyMock.getSurveyInfo()).thenReturn(surveyInfoMock);

        doNothing().when(surveyMock).updateSurveyInfo(any(SurveyInfo.class));

        SurveyResponseDto expectedResponseDto = new SurveyResponseDto(
                id,
               title,
                description,
                maxSurveyee,
                pointPerPerson,
                maxSurveyee * pointPerPerson,
                deadline,
                expectedTime,
                SurveyStatus.NOT_STARTED,
                0L
        );

        when(surveyMapper.toResponseDto(surveyMock)).thenReturn(expectedResponseDto);

        //when
        SurveyResponseDto responseDto = surveyService.updateSurveyInfo(userId, 1L, updateRequestDto);

        //then
        assertThat(responseDto.getTitle()).isEqualTo(title);
        assertThat(responseDto.getDescription()).isEqualTo(description);
        assertThat(responseDto.getMaxSurveyee()).isEqualTo(maxSurveyee);
        assertThat(responseDto.getTotalPoint()).isEqualTo(maxSurveyee * pointPerPerson);

        verify(userReader).validateUserIdOrThrow(userId);
        verify(surveyMock).updateSurveyInfo(any(SurveyInfo.class));
        verify(surveyMapper).toResponseDto(surveyMock);

    }

    @Test
    @DisplayName("기능_설문 상태 변경을 성공한다")
    void 설문_상태를_변경한다(){
        //given
        Long userId = 1L;
        Long id = 1L;
        SurveyStatus status = SurveyStatus.IN_PROGRESS;

        SurveyStatusUpdateRequestDto updateRequestDto = new SurveyStatusUpdateRequestDto(status);

        Survey surveyMock = mock(Survey.class);

        doNothing().when(userReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(id)).thenReturn(surveyMock);
        when(surveyMock.isUserSurveyCreator(anyLong())).thenReturn(true);
        doNothing().when(surveyMock).changeSurveyStatus(status);
        when(surveyMock.getStatus()).thenReturn(status);

        //when
        SurveyStatusResponseDto responseDto = surveyService.updateSurveyStatus(userId, id, updateRequestDto);

        //then

        assertThat(responseDto.getStatus()).isEqualTo(status);

        verify(surveyMock, times(1)).getStatus();
        verify(surveyMock).changeSurveyStatus(status);
    }

    @Test
    @DisplayName("기능_설문 삭제를 성공한다")
    void 설문을_삭제한다(){
        //given
        Long userId = 1L;
        Long id = 1L;

        Survey surveyMock = mock(Survey.class);
        Question questionMock1 = mock(Question.class);
        Question questionMock2 = mock(Question.class);
        ReflectionTestUtils.setField(questionMock2, "id", 2L);

        List<Question> questionMockList = List.of(questionMock1, questionMock2);
        doNothing().when(userReader).validateUserIdOrThrow(userId);
        when(surveyQueryService.findSurvey(id)).thenReturn(surveyMock);
        when(surveyMock.isUserSurveyCreator(anyLong())).thenReturn(true);
        when(surveyMock.isInProgress()).thenReturn(false);

        doNothing().when(surveyMock).deleteSurvey();

        //when
        surveyService.deleteSurvey(userId, id);

        //then
        verify(surveyMock).deleteSurvey();

    }

    // 참여자 테스트
    @Test
    @DisplayName("기능: 설문 상세조회를 성공한다")
    void success_getSurvey() {
        // given
        Long surveyId = 1L;
        Survey survey = mock(Survey.class);
        SurveyResponseDto dto = mock(SurveyResponseDto.class);

        when(surveyQueryService.findSurvey(surveyId)).thenReturn(survey);
        when(surveyMapper.toResponseDto(survey)).thenReturn(dto);
        when(surveyAnswerFacade.getParticipatedCount(surveyId)).thenReturn(10L);

        // when
        SurveyResponseDto result = surveyService.getSurvey(surveyId);

        // then
        assertThat(result).isEqualTo(dto);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyMapper).toResponseDto(survey);
        verify(surveyAnswerFacade).getParticipatedCount(surveyId);
        verify(dto).changeSurveyeeCount(10L);
    }

    @Test
    @DisplayName("기능: 설문 시작, 설문지를 조회한다.")
    void success_startSurvey() {

        //given
        Long userId = 1L;
        Long surveyId = 100L;

        Survey survey = mock(Survey.class);
        SurveyInfo surveyInfo = mock(SurveyInfo.class);
        SurveyQuestionDto expectedDto = mock(SurveyQuestionDto.class);

        doNothing().when(userReader).validateUserIdOrThrow(userId);

        when(surveyQueryService.findSurvey(surveyId))
                .thenReturn(survey);

        when(survey.isInProgress()).thenReturn(true);
        when(survey.getSurveyInfo()).thenReturn(surveyInfo);
        when(surveyInfo.getDeadline()).thenReturn(LocalDateTime.of(2025, 9, 10, 11, 20));

        doNothing().when(surveyAnswerFacade).validateParticipated(userId, surveyId);

        when(surveyMapper.toSurveyQuestionDto(survey))
                .thenReturn(expectedDto);

        SurveyQuestionDto result = surveyService.startSurvey(userId, surveyId);

        // then
        assertThat(result).isEqualTo(expectedDto);

        verify(userReader).validateUserIdOrThrow(userId);
        verify(surveyQueryService).findSurvey(surveyId);
        verify(surveyAnswerFacade).validateParticipated(userId, surveyId);
        verify(surveyMapper).toSurveyQuestionDto(survey);
    }

//    @Test
//    @DisplayName("기능: 설문 응답을 저장한다.")
//    void success_saveSurveyAnswer() {
//
//        //given
//        Long userId = 1L;
//        Long surveyId = 1L;
//        Long questionId = 1L;
//
//        User user = UserFixtureGenerator.generateUserFixture();
//        Survey survey = SurveyFixtureGenerator.generateSurveyFixture();
//        ReflectionTestUtils.setField(survey, "status", SurveyStatus.IN_PROGRESS);
//        ReflectionTestUtils.setField(survey, "deadline", LocalDateTime.now().plusDays(1));
//
//        Question question = mock(Question.class);
//
//        QuestionAnswerRequestDto questionAnswerRequestDto = mock(QuestionAnswerRequestDto.class);
//        ReflectionTestUtils.setField(questionAnswerRequestDto, "number", questionId);
//        List<QuestionAnswerRequestDto> answers = List.of(questionAnswerRequestDto);
//        SurveyAnswerRequestDto surveyAnswerRequestDto = new SurveyAnswerRequestDto(answers);
//
//        SurveyQuestionStrategy surveyQuestionStrategy = mock(SurveyQuestionStrategy.class);
//        when(surveyQuestionStrategy.isSupport(question.getType())).thenReturn(true);
//        when(surveyQuestionStrategies.stream()).thenReturn(Stream.of(surveyQuestionStrategy));
//
//        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(survey));
//        when(userFacade.findUser(userId)).thenReturn(user);
//        when(surveyAnswerRepository.existsBySurveyIdAndUserId(surveyId, userId)).thenReturn(false);
//        when(surveyAnswerRepository.save(any(SurveyAnswer.class))).thenReturn(mock(SurveyAnswer.class));
//        when(questionRepository.findById(questionAnswerRequestDto.getNumber())).thenReturn(Optional.of(question));
//        when(surveyAnswerRepository.countBySurveyId(surveyId)).thenReturn(survey.getMaxSurveyee()-1);
//
//        //when
//        surveyService.saveSurveyAnswer(surveyId, surveyAnswerRequestDto, userId);
//
//        //then
//        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
//        verify(userFacade).findUser(userId);
//        verify(surveyAnswerRepository).existsBySurveyIdAndUserId(surveyId, userId);
//        verify(surveyAnswerRepository).save(any(SurveyAnswer.class));
//        verify(questionRepository).findById(questionAnswerRequestDto.getNumber());
//        verify(surveyAnswerRepository).countBySurveyId(surveyId);
//
//    }
//
//    @Test
//    @DisplayName("기능: 참여한 설문 목록을 조회한다.")
//    void success_getSurveyeeSurveyList() {
//
//        // given
//        Long userId = 1L;
//        User user = UserFixtureGenerator.generateUserFixture();
//        Survey survey = SurveyFixtureGenerator.generateSurveyFixture();
//        SurveyAnswer surveyAnswer = mock(SurveyAnswer.class);
//
//        //when(surveyAnswer.getSurveyId()).thenReturn(survey);
//
//        when(userFacade.findUser(userId)).thenReturn(user);
//        when(surveyAnswerRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(surveyAnswer));
//
//        // when
//        SurveyeeSurveyListDto result = surveyService.getUserSurveyAnswerHistory(userId);
//
//        // then
//        assertThat(result).isNotNull();
//
//    }
}
