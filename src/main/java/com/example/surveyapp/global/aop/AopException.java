package com.example.surveyapp.global.aop;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class AopException extends BaseException {
    public AopException(ErrorCode errorCode) {
        super(errorCode);
    }
}
