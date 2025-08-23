package com.example.surveyapp.domain.point.exception;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PointErrorCode implements ErrorCode {
    //포인트 에러
    POINT_NOT_ENOUGH(HttpStatus.BAD_REQUEST,"포인트가 부족합니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "포인트가 존재하지 않습니다."),
    POINT_INVALID_AMOUNT(HttpStatus.BAD_REQUEST,"5000원부터 충전 가능합니다."),
    POINT_EARN_FAILED(HttpStatus.BAD_REQUEST,"포인트 지급에 실패했습니다."),
    POINT_MINIMUM_AMOUNT(HttpStatus.BAD_REQUEST,"포인트가 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    CANNOT_CONVERT_PAYLOAD(HttpStatus.BAD_REQUEST, "이벤트 직렬화 실패");

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
