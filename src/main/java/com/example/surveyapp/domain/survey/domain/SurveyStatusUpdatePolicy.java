package com.example.surveyapp.domain.survey.domain;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class SurveyStatusUpdatePolicy {
    public void validateStatus(SurveyStatus currentStatus, SurveyStatus newStatus){
        if(currentStatus.equals(newStatus)){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(newStatus.isNotStarted()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(currentStatus.isDone() && newStatus.isInProgress()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(!currentStatus.isInProgress() && newStatus.isPaused()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
        if(currentStatus.isNotStarted() && newStatus.isDone()){
            throw new CustomException(ErrorCode.INVALID_SURVEY_STATUS_TRANSITION);
        }
    }
}
