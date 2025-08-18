package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.presentation.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.OptionUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.global.reader.UserReader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionsService {

    private final UserReader userReader;
    private final SurveyValidator surveyValidator = new SurveyValidator();
    private final SurveyQueryService surveyQueryService;
    private final SurveyQuestionService surveyQuestionService = new SurveyQuestionService();

    @Transactional
    public OptionResponseDto createOption(Long userId, Long surveyId, Long questionId, OptionCreateRequestDto requestDto){

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQueryService.findSurvey(surveyId);
        Question question = surveyQuestionService.getQuestionById(survey, questionId);

        surveyValidator.validateUpdatable(userId, survey);

        Options option = new Options(
                requestDto.getNumber(),
                requestDto.getContent()
        );

        question.addOption(option);

        return new OptionResponseDto(option.getId(), option.getNumber(), option.getContent());
    }

    //(inprogress일때는 모두, 다른 상태에는 관리자랑 해당 설문 출제자만 조회가능)
    @Transactional(readOnly = true)
    public List<OptionResponseDto> getOptions(Long userId, Long surveyId, Long questionId){

        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQueryService.findSurvey(surveyId);
        Question question = surveyQuestionService.getQuestionById(survey, questionId);

        surveyValidator.validateQuestionAccess(userId, survey, userReader.validateUserRoleToSurveyee(userId));

        List<Options> optionsList = question.getOptions();

        return optionsList.stream()
                .map(option -> new OptionResponseDto(
                        option.getId(),
                        option.getNumber(),
                        option.getContent()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public OptionResponseDto updateOption(Long userId, Long surveyId, Long questionId, Long optionId, OptionUpdateRequestDto requestDto){

        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQueryService.findSurvey(surveyId);
        Question question = surveyQuestionService.getQuestionById(survey, questionId);
        surveyValidator.validateUpdatable(userId, survey);

        Options option = question.updateOption(optionId, requestDto.getNumber(), requestDto.getContent());

        return new OptionResponseDto(
                option.getId(),
                option.getNumber(),
                option.getContent());
    }

    @Transactional
    public void deleteOption(Long userId, Long surveyId, Long questionId, Long optionId){

        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQueryService.findSurvey(surveyId);
        Question question = surveyQuestionService.getQuestionById(survey, questionId);

        surveyValidator.validateDeletable(userId, survey);

        question.deleteOptionById(optionId);
    }
}
