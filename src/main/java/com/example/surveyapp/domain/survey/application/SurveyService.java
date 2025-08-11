package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.domain.SurveyStatusUpdatePolicy;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.application.mapper.SurveyMapper;
import com.example.surveyapp.domain.survey.controller.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.SurveyStatusUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.SurveyUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.*;
import com.example.surveyapp.domain.survey.domain.model.entity.*;
import com.example.surveyapp.domain.survey.controller.dto.response.SurveyQuestionDto;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyPoints;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.event.SurveyCreateEvent;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SurveyService {


    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyStatusUpdatePolicy statusUpdatePolicy;
    private final SurveyQuestionQueryService surveyQuestionQueryService;
    private final SurveyValidator surveyValidator;
    private final UserReader userReader;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SurveyResponseDto createSurvey(Long userId, SurveyCreateRequestDto requestDto) {

        userReader.validateUserIdOrThrow(userId);

        SurveyInfo surveyInfo = surveyMapper.toSurveyInfo(requestDto);

        Survey survey = Survey.of(userId, surveyInfo);
        Survey saved = surveyRepository.save(survey);

        if(userReader.validateUserRole(userId, UserRoleEnum.SURVEYOR)){
            /// /////////// 이벤트 처리될 부분
            eventPublisher.publishEvent(new SurveyCreateEvent(saved, userId));
            /// ///////////
        }

        return surveyMapper.toResponseDto(saved);
    }

    //삭제되지 않은 설문만
    @Transactional(readOnly = true)
    public PageSurveyResponseDto<SurveyResponseDto> getSurveys(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveyPage = surveyRepository.findAllSurveyPaged(pageable);

        Page<SurveyResponseDto> surveyResponseDtoPage = surveyPage.map(surveyMapper::toResponseDto);

        return new PageSurveyResponseDto<>(surveyResponseDtoPage);
    }

    @Transactional
    public SurveyResponseDto updateSurveyInfo(Long userId, Long surveyId, SurveyUpdateRequestDto requestDto) {

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateUpdatable(userId, survey);

        SurveyInfo oldSurveyInfo = survey.getSurveyInfo();
        SurveyInfo newSurveyInfo = requestDto.toNewSurveyInfo(oldSurveyInfo);

        survey.updateSurveyInfo(newSurveyInfo);

        return surveyMapper.toResponseDto(survey);
    }

    @Transactional
    //설문 상태 변경(NOT_STARTED -> IN_PROGRESS, IN_PROGRESS -> PAUSED, PAUSED -> IN_PROGRESS, IN_PROGRESS -> DONE)
    public SurveyStatusResponseDto updateSurveyStatus(Long userId, Long surveyId, SurveyStatusUpdateRequestDto requestDto) {

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);

        surveyValidator.validateStatusUpdatable(userId, survey);
        statusUpdatePolicy.validateStatus(survey.getStatus(), requestDto.getStatus());

        survey.changeSurveyStatus(requestDto.getStatus());

        return new SurveyStatusResponseDto(survey.getStatus());
    }

    @Transactional
    public void deleteSurvey(Long userId, Long surveyId) {

        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateDeletable(userId, survey);

        survey.deleteSurvey();
    }

    // 참여자 API
    // 삭제 되지 않은 설문만 설문 상세 조회
    @Transactional
    public SurveyResponseDto getSurvey(Long surveyId) {

        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        SurveyResponseDto responseDto = surveyMapper.toResponseDto(survey);
        responseDto.changeSurveyeeCount(surveyAnswerRepository.countBySurveyId(surveyId));

        return responseDto;
    }

    // 설문 시작
    @Transactional(readOnly = true)
    public SurveyQuestionDto startSurvey(Long userId, Long surveyId) {
        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQuestionQueryService.findSurveyWithQuestionsAndOptions(surveyId);
        surveyValidator.validateStartable(survey);
        surveyValidator.validateNotParticipated(userId, surveyId);

        return surveyMapper.toSurveyQuestionDto(survey);
    }

}
