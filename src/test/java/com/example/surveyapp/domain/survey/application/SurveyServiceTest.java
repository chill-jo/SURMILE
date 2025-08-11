package com.example.surveyapp.domain.survey.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.surveyapp.config.generator.UserFixtureGenerator;
import com.example.surveyapp.domain.point.service.PointService;
import com.example.surveyapp.domain.survey.application.mapper.SurveyMapper;
import com.example.surveyapp.domain.survey.controller.dto.request.*;
import com.example.surveyapp.domain.survey.controller.dto.response.*;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.survey.domain.repository.OptionsRepository;
import com.example.surveyapp.domain.survey.domain.repository.QuestionRepository;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import com.example.surveyapp.domain.survey.facade.UserFacade;
import com.example.surveyapp.domain.surveyanswer.domain.strategy.SurveyQuestionStrategy;
import com.example.surveyapp.domain.surveyanswer.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.controller.dto.request.SurveyAnswerRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.SurveyQuestionDto;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyeeSurveyListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.surveyapp.config.generator.SurveyFixtureGenerator;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.user.domain.model.User;
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
    private QuestionRepository questionRepository;

    @Mock
    private OptionsRepository optionsRepository;

    @Mock
    private SurveyAnswerRepository surveyAnswerRepository;

    @Mock
    private SurveyMapper surveyMapper;

    @Mock
    private UserFacade userFacade;

    @Mock
    private PointService pointService;

    @Mock
    private List<SurveyQuestionStrategy> surveyQuestionStrategies;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    @DisplayName("기능_설문 생성을 성공한다")
    void 설문을_생성한다(){
        //Given
        User userMock = mock(User.class);
        Long userId = 1L;

        String title = "테스트 설문 제목";
        String description = "테스트 설문 설명";
        Long maxSurveyee = 50L;
        Long pointPerPerson = 100L;
        LocalDateTime deadline = LocalDateTime.of(2025, 7,25, 15,30);
        Long expectedTime = 20L;

        SurveyCreateRequestDto surveyCreateRequestDto = new SurveyCreateRequestDto(
                title, description, maxSurveyee, pointPerPerson, deadline, expectedTime);


        Survey surveyMock = SurveyFixtureGenerator.generateSurveyFixture();
        Survey saved = SurveyFixtureGenerator.generateSurveyFixture();

        when(userFacade.findUser(userId)).thenReturn(userMock);

        when(surveyMapper.createSurveyEntity(surveyCreateRequestDto, userMock))
                .thenReturn(surveyMock);

        when(surveyRepository.save(surveyMock))
                .thenReturn(saved);

        when(surveyMapper.toResponseDto(saved))
                .thenReturn(new SurveyResponseDto(
                        surveyMock.getId(),
                        surveyMock.getTitle(),
                        surveyMock.getDescription(),
                        surveyMock.getMaxSurveyee(),
                        surveyMock.getPointPerPerson(),
                        surveyMock.getTotalPoint(),
                        surveyMock.getDeadline(),
                        surveyMock.getExpectedTime(),
                        surveyMock.getStatus(),
                        0L
                        )
                );

        //When
        SurveyResponseDto surveyResponseDto = surveyService.createSurvey(userId, surveyCreateRequestDto);

        //Then
        assertThat(surveyResponseDto.getTitle()).isEqualTo(title);
        assertThat(surveyResponseDto.getDescription()).isEqualTo(description);
        assertThat(surveyResponseDto.getMaxSurveyee()).isEqualTo(maxSurveyee);
        assertThat(surveyResponseDto.getPointPerPerson()).isEqualTo(pointPerPerson);
        assertThat(surveyResponseDto.getDeadline()).isEqualTo(deadline);
        assertThat(surveyResponseDto.getExpectedTime()).isEqualTo(expectedTime);

        verify(surveyMapper).createSurveyEntity(surveyCreateRequestDto, userMock);
        verify(surveyRepository, times(1))
                .save(surveyMock);
        verify(surveyMapper).toResponseDto(saved);
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
                        surveyMock1.getTitle(),
                        surveyMock1.getDescription(),
                        surveyMock1.getMaxSurveyee(),
                        surveyMock1.getPointPerPerson(),
                        surveyMock1.getTotalPoint(),
                        surveyMock1.getDeadline(),
                        surveyMock1.getExpectedTime(),
                        surveyMock1.getStatus(),
                        0L
        ));
        when(surveyMapper.toResponseDto(surveyMock2))
                .thenReturn(new SurveyResponseDto(
                        surveyMock2.getId(),
                        surveyMock2.getTitle(),
                        surveyMock2.getDescription(),
                        surveyMock2.getMaxSurveyee(),
                        surveyMock2.getPointPerPerson(),
                        surveyMock2.getTotalPoint(),
                        surveyMock2.getDeadline(),
                        surveyMock2.getExpectedTime(),
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
    void 설문_출제자가_설문_상세정보를_수정한다(){
        //given
        User userMock = mock(User.class);
        Long userId = 1L;
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

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(surveyMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);
        when(surveyMock.isNotStarted()).thenReturn(true);

        doNothing().when(surveyMapper).updateSurvey(updateRequestDto, surveyMock);

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
        SurveyResponseDto responseDto = surveyService.updateSurvey(userId, 1L, updateRequestDto);

        //then
        assertThat(responseDto.getTitle()).isEqualTo(title);
        assertThat(responseDto.getDescription()).isEqualTo(description);
        assertThat(responseDto.getMaxSurveyee()).isEqualTo(maxSurveyee);
        assertThat(responseDto.getTotalPoint()).isEqualTo(maxSurveyee * pointPerPerson);

        verify(surveyRepository).findByIdAndIsDeletedFalse(id);
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(userMock, never()).isUserRoleNotAdmin();
        verify(surveyMock).isNotStarted();
        verify(surveyMapper).updateSurvey(updateRequestDto, surveyMock);
        verify(surveyMapper).toResponseDto(surveyMock);

    }

    @Test
    @DisplayName("기능_설문 상태 변경을 성공한다")
    void 관리자가_설문_상태를_변경한다(){
        //given
        User userMock = mock(User.class);
        Long userId = 1L;
        Long id = 1L;
        SurveyStatus status = SurveyStatus.IN_PROGRESS;

        SurveyStatusUpdateRequestDto updateRequestDto = new SurveyStatusUpdateRequestDto(status);

        Survey surveyMock = mock(Survey.class);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(surveyMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(false);
        when(userMock.isUserRoleNotAdmin()).thenReturn(false);

        doNothing().when(surveyMock).changeSurveyStatus(status);
        when(surveyMock.getStatus()).thenReturn(status);
        //when
        SurveyStatusResponseDto responseDto = surveyService.updateSurveyStatus(userId, id, updateRequestDto);

        //then

        assertThat(responseDto.getStatus()).isEqualTo(status);

        verify(surveyRepository).findByIdAndIsDeletedFalse(id);
        verify(userMock).isUserRoleNotAdmin();
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(surveyMock, times(1)).getStatus();
        verify(surveyMock).changeSurveyStatus(status);

    }

    @Test
    @DisplayName("기능_설문 삭제를 성공한다")
    void 설문을_삭제한다(){
        //given
        User userMock = mock(User.class);
        Long userId = 1L;
        Long id = 1L;

        Survey surveyMock = mock(Survey.class);
        Question questionMock1 = mock(Question.class);
        Question questionMock2 = mock(Question.class);
        ReflectionTestUtils.setField(questionMock2, "id", 2L);

        List<Question> questionMockList = List.of(questionMock1, questionMock2);

        when(userFacade.findUser(userId)).thenReturn(userMock);
        when(surveyRepository.findById(id)).thenReturn(Optional.of(surveyMock));

        when(surveyMock.isUserSurveyCreator(userMock)).thenReturn(true);

        when(surveyMock.isDeleted()).thenReturn(false);

        when(surveyMock.isInProgress()).thenReturn(false);

        when(questionRepository.findAllBySurvey(surveyMock)).thenReturn(questionMockList);

        doNothing().when(surveyMock).deleteSurvey();

        //when
        surveyService.deleteSurvey(userId, id);

        //then
        verify(userFacade).findUser(userId);
        verify(surveyRepository).findById(id);
        verify(surveyMock).isUserSurveyCreator(userMock);
        verify(surveyMock).isDeleted();
        verify(surveyMock).isInProgress();
        verify(optionsRepository).deleteAllByQuestion(questionMock1);
        verify(optionsRepository).deleteAllByQuestion(questionMock2);
        verify(questionRepository).deleteAllBySurvey(surveyMock);
        verify(surveyMock).deleteSurvey();

    }


    // 참여자 테스트
    @Test
    @DisplayName("기능: 설문 상세조회를 성공한다")
    void success_getSurvey() {

        //given
        Long surveyId = 1L;
        Long surveyeeCount = 1L;
        Survey survey = SurveyFixtureGenerator.generateSurveyFixture();
        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(survey));
        when(surveyMapper.toResponseDto(survey))
                .thenReturn(new SurveyResponseDto(
                                survey.getId(),
                                survey.getTitle(),
                                survey.getDescription(),
                                survey.getMaxSurveyee(),
                                survey.getPointPerPerson(),
                                survey.getTotalPoint(),
                                survey.getDeadline(),
                                survey.getExpectedTime(),
                                survey.getStatus(),
                                0L
                        )
                );

        when(surveyAnswerRepository.countBySurveyId(surveyId)).thenReturn(surveyeeCount);

        //when
        SurveyResponseDto result = surveyService.getSurvey(surveyId);

        //then
        assertThat(result.getSurveyeeCount()).isEqualTo(surveyeeCount);
    }

    @Test
    @DisplayName("기능: 설문 시작, 설문지를 조회한다.")
    void success_startSurvey() {

        //given
        Long userId = 1L;
        Long surveyId = 1L;
        User user = UserFixtureGenerator.generateUserFixture();
        Survey survey = SurveyFixtureGenerator.generateSurveyFixture();
        ReflectionTestUtils.setField(survey, "status", SurveyStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(survey, "deadline", LocalDateTime.now().plusDays(1));

        List<Question> questionList = new ArrayList<>();
        Question question = mock(Question.class);
        questionList.add(question);

        List<OptionResponseDto> optionsList = new ArrayList<>();
        OptionResponseDto optionResponseDto = mock(OptionResponseDto.class);
        optionsList.add(optionResponseDto);

        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(survey));
        when(userFacade.findUser(userId)).thenReturn(user);
        when(surveyAnswerRepository.existsBySurveyIdAndUserId(surveyId, userId)).thenReturn(false);
        when(questionRepository.findAllBySurveyIdOrderByNumberASC(surveyId)).thenReturn(questionList);
        when(optionsRepository.findAllByQuestionIdOrderByNumberAsc(question.getId())).thenReturn(optionsList);

        //when
        SurveyQuestionDto result = surveyService.startSurvey(userId, surveyId);

        //then
        assertThat(result).isNotNull();

    }

    @Test
    @DisplayName("기능: 설문 응답을 저장한다.")
    void success_saveSurveyAnswer() {

        //given
        Long userId = 1L;
        Long surveyId = 1L;
        Long questionId = 1L;

        User user = UserFixtureGenerator.generateUserFixture();
        Survey survey = SurveyFixtureGenerator.generateSurveyFixture();
        ReflectionTestUtils.setField(survey, "status", SurveyStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(survey, "deadline", LocalDateTime.now().plusDays(1));

        Question question = mock(Question.class);

        QuestionAnswerRequestDto questionAnswerRequestDto = mock(QuestionAnswerRequestDto.class);
        ReflectionTestUtils.setField(questionAnswerRequestDto, "number", questionId);
        List<QuestionAnswerRequestDto> answers = List.of(questionAnswerRequestDto);
        SurveyAnswerRequestDto surveyAnswerRequestDto = new SurveyAnswerRequestDto(answers);

        SurveyQuestionStrategy surveyQuestionStrategy = mock(SurveyQuestionStrategy.class);
        when(surveyQuestionStrategy.isSupport(question.getType())).thenReturn(true);
        when(surveyQuestionStrategies.stream()).thenReturn(Stream.of(surveyQuestionStrategy));

        when(surveyRepository.findByIdAndIsDeletedFalse(surveyId)).thenReturn(Optional.of(survey));
        when(userFacade.findUser(userId)).thenReturn(user);
        when(surveyAnswerRepository.existsBySurveyIdAndUserId(surveyId, userId)).thenReturn(false);
        when(surveyAnswerRepository.save(any(SurveyAnswer.class))).thenReturn(mock(SurveyAnswer.class));
        when(questionRepository.findById(questionAnswerRequestDto.getNumber())).thenReturn(Optional.of(question));
        when(surveyAnswerRepository.countBySurveyId(surveyId)).thenReturn(survey.getMaxSurveyee()-1);

        //when
        surveyService.saveSurveyAnswer(surveyId, surveyAnswerRequestDto, userId);

        //then
        verify(surveyRepository).findByIdAndIsDeletedFalse(surveyId);
        verify(userFacade).findUser(userId);
        verify(surveyAnswerRepository).existsBySurveyIdAndUserId(surveyId, userId);
        verify(surveyAnswerRepository).save(any(SurveyAnswer.class));
        verify(questionRepository).findById(questionAnswerRequestDto.getNumber());
        verify(surveyAnswerRepository).countBySurveyId(surveyId);

    }

    @Test
    @DisplayName("기능: 참여한 설문 목록을 조회한다.")
    void success_getSurveyeeSurveyList() {

        // given
        Long userId = 1L;
        User user = UserFixtureGenerator.generateUserFixture();
        Survey survey = SurveyFixtureGenerator.generateSurveyFixture();
        SurveyAnswer surveyAnswer = mock(SurveyAnswer.class);

        //when(surveyAnswer.getSurveyId()).thenReturn(survey);

        when(userFacade.findUser(userId)).thenReturn(user);
        when(surveyAnswerRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(surveyAnswer));

        // when
        SurveyeeSurveyListDto result = surveyService.getUserSurveyAnswerHistory(userId);

        // then
        assertThat(result).isNotNull();

    }
}
