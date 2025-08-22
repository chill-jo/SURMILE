package com.example.surveyapp.domain.survey.exception;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public enum SurveyErrorCode implements ErrorCode {
    //설문 에러
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력 값이 유효하지 않습니다."),
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 설문입니다."),
    SURVEY_ALREADY_DELETED(HttpStatus.GONE, "이미 삭제된 설문입니다."),
    SURVEY_CANNOT_BE_DELETED(HttpStatus.CONFLICT, "현재 설문 상태에서는 삭제할 수 없습니다."),
    INVALID_SURVEY_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "설문 상태를 변경할 수 없습니다."),
    SURVEY_CANNOT_BE_MODIFIED(HttpStatus.CONFLICT, "설문 상세정보를 수정할 수 없습니다."),
    SURVEYEE_NOT_ALLOWED(HttpStatus.UNAUTHORIZED, "참여자 권한으로는 요청이 불가합니다."),
    SURVEY_STARTED(HttpStatus.FORBIDDEN, "설문 참여가 시작되었을 때는 요청이 불가합니다."),
    INVALID_SURVEY_POINT(HttpStatus.BAD_REQUEST, "설문 지급 포인트가 잘못되었습니다."),    //질문 에러
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 질문입니다."),
    NOT_SURVEY_CREATOR(HttpStatus.UNAUTHORIZED, "해당 설문 출제자가 아니면 요청이 불가합니다."),
    QUESTION_NOT_FROM_SURVEY(HttpStatus.NOT_FOUND, "질문이 설문에 존재하지 않습니다."),
    OPTIONS_NOT_FROM_SURVEY(HttpStatus.NOT_FOUND, "선택지가 질문에 존재하지 않습니다."),
    SURVEY_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "진행 중이 아닌 설문에는 참여할 수 없습니다."),
    NO_QUESTIONS_IN_SURVEY(HttpStatus.NO_CONTENT, "설문에 질문이 존재하지 않아 설문 참여를 시작할 수 없습니다."),
    //선택지 에러
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 선택지입니다."),
    OPTION_INVALID_FOR_SUBJECTIVE_QUESTION(HttpStatus.BAD_REQUEST, "주관식 질문에는 선택지를 생성할 수 없습니다."),

    //outbox 에러
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
