package com.example.surveyapp.domain.user.exception;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    //회원
    IS_BLACKLIST(HttpStatus.BAD_REQUEST, "해당 회원은 이미 블랙입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EXISTS_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    EXISTS_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 별명입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "적합하지 않은 닉네임입니다."),
    NOT_MATCH_USER_ROLE(HttpStatus.CONFLICT, "유저 권한이 일치하지 않습니다."),
    MISSING_BASE_DATA_CATEGORIES(HttpStatus.BAD_REQUEST, "모든 기초 정보 항목을 입력해주세요.");

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
