package com.example.surveyapp.domain.survey.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class SurveyException extends BaseException {

    public SurveyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
