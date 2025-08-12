package com.example.surveyapp.domain.admin.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class AdminException extends BaseException {
    public AdminException(ErrorCode errorCode) {

        super(errorCode);
    }
}
