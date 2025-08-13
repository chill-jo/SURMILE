package com.example.surveyapp.domain.surveyanswer.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class AnswerException extends BaseException {
    public AnswerException(ErrorCode errorCode) {

        super(errorCode);
    }
}
