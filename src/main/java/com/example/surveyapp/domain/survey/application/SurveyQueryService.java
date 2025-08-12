package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.exception.SurveyErrorCode;
import com.example.surveyapp.domain.survey.exception.SurveyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class SurveyQueryService {

    private final SurveyRepository surveyRepository;

    public Survey findSurvey(Long surveyId){
        return surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }

    public Survey findSurveyWithQuestionsAndOptions(Long surveyId){
        return surveyRepository.findSurveyWithQuestionsAndOptions(surveyId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }
}
