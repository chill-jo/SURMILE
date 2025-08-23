package com.example.surveyapp.domain.surveyanswer.exception;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AnswerErrorCode implements ErrorCode {
    SURVEY_ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "이미 설문에 응답하셨습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력 값이 유효하지 않습니다."),
    CANNOT_CONVERT_PAYLOAD(HttpStatus.UNAUTHORIZED,"이벤트 직렬화 실패");

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
