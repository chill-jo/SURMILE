package com.example.surveyapp.domain.survey.application;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapp.domain.ai.moderation.application.facade.AiModerationFacade;
import com.example.surveyapp.domain.survey.application.facade.SurveyAnswerFacade;
import com.example.surveyapp.domain.survey.application.mapper.SurveyMapper;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyStatusUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.PageSurveyResponseDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyQuestionDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyResponseDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyStatusResponseDto;
import com.example.surveyapp.global.reader.UserReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final UserReader userReader;
	private final ApplicationEventPublisher eventPublisher;
	private final SurveyRepository surveyRepository;
	private final SurveyMapper surveyMapper;
	private final SurveyQueryService surveyQueryService;
	private final SurveyValidator surveyValidator = new SurveyValidator();
	private final SurveyAnswerFacade surveyAnswerFacade;
	private final AiModerationFacade aiModerationFacade;

	@Transactional
	public SurveyResponseDto createSurvey(Long userId, SurveyCreateRequestDto requestDto) {

		userReader.validateUserIdOrThrow(userId);
		aiModerationFacade.checkTitleModeration(requestDto.getTitle());
		aiModerationFacade.checkDescriptionModeration(requestDto.getDescription());

		SurveyInfo surveyInfo = surveyMapper.toSurveyInfo(requestDto);

		Survey survey = Survey.of(userId, surveyInfo);
		Survey saved = surveyRepository.save(survey);

		if (userReader.validateUserRoleToSurveyor(userId)) {
			eventPublisher.publishEvent(new SurveyCreateEvent(
				saved.getId(),
				saved.getSurveyInfo().getTotalPoint().getValue(),
				userId)
			);
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
		Survey survey = surveyQueryService.findSurvey(surveyId);
		surveyValidator.validateUpdatable(userId, survey);
		aiModerationFacade.checkTitleModeration(requestDto.getTitle());
		aiModerationFacade.checkDescriptionModeration(requestDto.getDescription());

		SurveyInfo oldSurveyInfo = survey.getSurveyInfo();
		SurveyInfo newSurveyInfo = requestDto.toNewSurveyInfo(oldSurveyInfo);

		survey.updateSurveyInfo(newSurveyInfo);

		return surveyMapper.toResponseDto(survey);
	}

	@Transactional
	//설문 상태 변경(NOT_STARTED -> IN_PROGRESS, IN_PROGRESS -> PAUSED, PAUSED -> IN_PROGRESS, IN_PROGRESS -> DONE)
	public SurveyStatusResponseDto updateSurveyStatus(Long userId, Long surveyId,
		SurveyStatusUpdateRequestDto requestDto) {

		userReader.validateUserIdOrThrow(userId);
		Survey survey = surveyQueryService.findSurvey(surveyId);

		surveyValidator.validateStatusUpdatable(userId, survey);

		survey.changeSurveyStatus(requestDto.getStatus());

		return new SurveyStatusResponseDto(survey.getStatus());
	}

	@Transactional
	public void deleteSurvey(Long userId, Long surveyId) {

		userReader.validateUserIdOrThrow(userId);

		Survey survey = surveyQueryService.findSurvey(surveyId);
		surveyValidator.validateDeletable(userId, survey);

		survey.deleteSurvey();
	}

	// 참여자 API
	// 삭제 되지 않은 설문만 설문 상세 조회
	@Transactional(readOnly = true)
	public SurveyResponseDto getSurvey(Long surveyId) {

		Survey survey = surveyQueryService.findSurvey(surveyId);
		SurveyResponseDto responseDto = surveyMapper.toResponseDto(survey);
		responseDto.changeSurveyeeCount(surveyAnswerFacade.getParticipatedCount(surveyId));

		return responseDto;
	}

	// 설문 시작
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "survey", key = "#surveyId")
	public SurveyQuestionDto startSurvey(Long userId, Long surveyId) {
		userReader.validateUserIdOrThrow(userId);

		Survey survey = surveyQueryService.findSurvey(surveyId);
		surveyValidator.validateStartable(survey);
		surveyAnswerFacade.validateParticipated(userId, surveyId);

		return surveyMapper.toSurveyQuestionDto(survey);
	}
}
