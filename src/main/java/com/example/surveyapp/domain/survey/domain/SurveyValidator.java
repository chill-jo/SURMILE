package com.example.surveyapp.domain.survey.domain;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.exception.SurveyErrorCode;
import com.example.surveyapp.domain.survey.exception.SurveyException;

import java.time.LocalDateTime;

public class SurveyValidator {

    public void validateStartable(Survey survey){
        if(!survey.isInProgress()){
            throw new SurveyException(SurveyErrorCode.SURVEY_NOT_IN_PROGRESS);
        }

        if(survey.getSurveyInfo().getDeadline().isBefore(LocalDateTime.now())){
            throw new SurveyException(SurveyErrorCode.SURVEY_NOT_IN_PROGRESS);
        }
    }

    public void validateUpdatable(Long userId, Survey survey){
        if(!survey.isNotStarted()){
            throw new SurveyException(SurveyErrorCode.SURVEY_STARTED);
        }
        if(!survey.isUserSurveyCreator(userId)){
            throw new SurveyException(SurveyErrorCode.NOT_SURVEY_CREATOR);
        }
    }

    public void validateStatusUpdatable(Long userId, Survey survey){
        if(!survey.isUserSurveyCreator(userId)){
            throw new SurveyException(SurveyErrorCode.NOT_SURVEY_CREATOR);
        }
    }

    public void validateDeletable(Long userId, Survey survey){
        if(!survey.isUserSurveyCreator(userId)){
            throw new SurveyException(SurveyErrorCode.NOT_SURVEY_CREATOR);
        }
        if(survey.isInProgress()){
            throw new SurveyException(SurveyErrorCode.SURVEY_CANNOT_BE_DELETED);
        }
    }

    public void validateQuestionAccess(Long userId, Survey survey, Boolean isUserSurveyee){
        if(!survey.isInProgress() && !survey.isUserSurveyCreator(userId)){
            throw new SurveyException(SurveyErrorCode.NOT_SURVEY_CREATOR);
        }
        if(!survey.isInProgress() && isUserSurveyee){
            throw new SurveyException(SurveyErrorCode.SURVEYEE_NOT_ALLOWED);
        }
    }
}
