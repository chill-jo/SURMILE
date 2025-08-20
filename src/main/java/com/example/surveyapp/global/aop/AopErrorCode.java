package com.example.surveyapp.global.aop;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AopErrorCode implements ErrorCode {

    FAILED_GET_LOCK(HttpStatus.LOCKED, "락 획득 실패했습니다.");

    private final HttpStatus status;

    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
