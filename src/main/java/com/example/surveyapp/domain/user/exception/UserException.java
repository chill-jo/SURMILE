package com.example.surveyapp.domain.user.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class UserException extends BaseException {
    public UserException(ErrorCode errorCode) {

        super(errorCode);
    }
}
