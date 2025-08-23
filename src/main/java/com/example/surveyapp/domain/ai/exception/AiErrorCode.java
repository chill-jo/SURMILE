package com.example.surveyapp.domain.ai.exception;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AiErrorCode implements ErrorCode {
    // 챗봇 에러
    NOT_MATCH_ADMIN(HttpStatus.UNAUTHORIZED, "관리자 권한만 사용할 수 있는 기능입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "챗봇 요청 형식이 올바르지 않습니다.");

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
