package com.example.surveyapp.domain.point.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class PointException extends BaseException {

    public PointException(ErrorCode errorCode) {
        super(errorCode);
    }
}
