package com.example.surveyapp.domain.ai.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class AiException extends BaseException {
    public AiException(ErrorCode errorCode) { super(errorCode); }
}
