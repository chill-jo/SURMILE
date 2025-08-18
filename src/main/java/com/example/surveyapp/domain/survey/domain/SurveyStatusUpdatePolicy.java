package com.example.surveyapp.domain.survey.domain;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.survey.exception.SurveyErrorCode;
import com.example.surveyapp.domain.survey.exception.SurveyException;

public class SurveyStatusUpdatePolicy {
    public void validateStatus(SurveyStatus currentStatus, SurveyStatus newStatus){
        if(currentStatus.equals(newStatus)){
            throw new SurveyException(SurveyErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(newStatus.isNotStarted() && !currentStatus.isPending()){
            throw new SurveyException(SurveyErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(currentStatus.isDone() && newStatus.isInProgress()){
            throw new SurveyException(SurveyErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(!currentStatus.isInProgress() && newStatus.isPaused()){
            throw new SurveyException(SurveyErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(currentStatus.isNotStarted() && newStatus.isDone()){
            throw new SurveyException(SurveyErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
    }
}
