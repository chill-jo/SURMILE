package com.example.surveyapp.global.response.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통 에러 정의
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러가 발생하였습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하는 HTTP 메서드가 아닙니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력 값이 유효하지 않습니다."),

    //회원
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EXISTS_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    EXISTS_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 별명입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "적합하지 않은 닉네임입니다."),

    //상품
    NOT_FOUND_PRODUCT(HttpStatus.UNAUTHORIZED,"존재하지 않는 상품입니다." ),
    NOT_SAME_PRODUCT_TITLE(HttpStatus.BAD_REQUEST,"동일한 상품명으로 수정이 불가합니다."),
    NOT_FOUND_PRODUCT_STATUS(HttpStatus.UNAUTHORIZED,"판매중인  상품이 아닙니다." ),
    NOT_SAME_CREATE_PRODUCT_TITLE(HttpStatus.BAD_REQUEST,"동일한 상품명으로 등록이 불가능합니다." ),

    //주문
    NOT_ORDER_USER(HttpStatus.UNAUTHORIZED,"주문이 불가한 계정입니다."),
    NOT_FOUND_POINT(HttpStatus.UNAUTHORIZED,"포인트가 존재하지 않습니다."),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST,"포인트가 부족합니다."),
    NOT_SURVEYEE_USER(HttpStatus.UNAUTHORIZED,"참여자 계정만 주문이 가능합니다." ),
    NOT_YOUR_ACCOUNT(HttpStatus.BAD_REQUEST,"본인이 아닌 다른 계정 조회는 불가능합니다." ),
    NOT_FOUND_ORDER(HttpStatus.UNAUTHORIZED,"해당 주문을 찾을 수 없습니다." ),
    NOT_YOUR_ORDER(HttpStatus.UNAUTHORIZED,"본인 주문만 확인 할 수 있습니다." ),
    NOT_SAME_ORDER_USER(HttpStatus.BAD_REQUEST,"본인이 주문한 주문만 삭제할 수 있습니다." ),

    //설문 에러
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 설문입니다."),
    SURVEY_ALREADY_DELETED(HttpStatus.GONE, "이미 삭제된 설문입니다."),
    SURVEY_CANNOT_BE_DELETED(HttpStatus.CONFLICT, "현재 설문 상태에서는 삭제할 수 없습니다."),
    INVALID_SURVEY_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "설문 상태를 변경할 수 없습니다."),
    SURVEY_CANNOT_BE_MODIFIED(HttpStatus.CONFLICT, "설문 상세정보를 수정할 수 없습니다."),
    SURVEYEE_NOT_ALLOWED(HttpStatus.UNAUTHORIZED, "참여자 권한으로는 요청이 불가합니다."),
    SURVEY_STARTED(HttpStatus.FORBIDDEN, "설문 참여가 시작되었을 때는 요청이 불가합니다."),
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "제목에 적합하지 않은 내용이 포함되어 있습니다."),
    INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, "상세 설명에 적합하지 않은 내용이 포함되어 있습니다."),

    //질문 에러
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 질문입니다."),
    NOT_SURVEY_CREATOR(HttpStatus.UNAUTHORIZED, "해당 설문 출제자가 아니면 요청이 불가합니다."),
    QUESTION_NOT_FROM_SURVEY(HttpStatus.NOT_FOUND, "질문이 설문에 존재하지 않습니다."),
    OPTIONS_NOT_FROM_SURVEY(HttpStatus.NOT_FOUND, "선택지가 질문에 존재하지 않습니다."),
    INVALID_QUESTION_CONTENT(HttpStatus.BAD_REQUEST, "질문 내용에 적합하지 않은 내용이 포함되어 있습니다."),

    //선택지 에러
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 선택지입니다."),
    OPTION_INVALID_FOR_SUBJECTIVE_QUESTION(HttpStatus.BAD_REQUEST, "주관식 질문에는 선택지를 생성할 수 없습니다."),
    INVALID_OPTION_CONTENT(HttpStatus.BAD_REQUEST, "선택지 내용에 적합하지 않은 내용이 포함되어 있습니다."),

    //설문 응답 등록 에러
    SURVEY_ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "이미 설문에 응답하셨습니다."),
    SURVEY_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "진행 중이 아닌 설문에는 참여할 수 없습니다."),



    //포인트 에러
    POINT_NOT_ENOUGH(HttpStatus.BAD_REQUEST,"포인트가 부족합니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "포인트가 존재하지 않습니다."),
    POINT_INVALID_AMOUNT(HttpStatus.BAD_REQUEST,"5000원부터 충전 가능합니다."),
    POINT_EARN_FAILED(HttpStatus.BAD_REQUEST,"포인트 지급에 실패했습니다."),
    POINT_MINIMUM_AMOUNT(HttpStatus.BAD_REQUEST,"포인트가 존재하지 않습니다."),

    //회원 기초 정보 관련 에러
    NOT_FOUNT_BASE_DATA(HttpStatus.NOT_FOUND, "기초 정보가 존재하지 않습니다."),
    EXISTS_BASE_DATA(HttpStatus.NOT_FOUND, "기초 정보가 존재합니다."),
    MISSING_BASE_DATA_CATEGORIES(HttpStatus.BAD_REQUEST, "모든 기초 정보 항목을 입력해주세요."),


    //관리자
    NOT_ADMIN_USER_ERROR(HttpStatus.UNAUTHORIZED,"관리자 계정으로 로그인하세요."),
    IS_BLACKLIST(HttpStatus.BAD_REQUEST, "해당 회원은 이미 블랙입니다."),
    IS_NOT_BLACKLIST(HttpStatus.BAD_REQUEST, "해당 회원은 블랙이 아닙니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}