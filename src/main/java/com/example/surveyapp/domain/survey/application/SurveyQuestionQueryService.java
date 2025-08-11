package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class SurveyQuestionQueryService {

    private final SurveyRepository surveyRepository;

    public Survey findSurvey(Long surveyId){
        return surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));
    }
}
