package com.example.surveyapp.domain.admin.exception;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
    //관리자
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_ADMIN_USER_ERROR(HttpStatus.UNAUTHORIZED,"관리자 계정으로 로그인하세요."),
    IS_BLACKLIST(HttpStatus.BAD_REQUEST, "해당 회원은 이미 블랙입니다."),
    IS_NOT_BLACKLIST(HttpStatus.BAD_REQUEST, "해당 회원은 블랙이 아닙니다.");
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
