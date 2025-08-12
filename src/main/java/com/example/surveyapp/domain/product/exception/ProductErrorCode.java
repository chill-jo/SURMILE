package com.example.surveyapp.domain.product.exception;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    //상품
    NOT_ADMIN_USER_ERROR(HttpStatus.UNAUTHORIZED,"관리자 계정으로 로그인하세요."),
    NOT_FOUND_PRODUCT(HttpStatus.UNAUTHORIZED,"존재하지 않는 상품입니다." ),
    NOT_SAME_PRODUCT_TITLE(HttpStatus.BAD_REQUEST,"동일한 상품명으로 수정이 불가합니다."),
    NOT_FOUND_PRODUCT_STATUS(HttpStatus.UNAUTHORIZED,"판매중인  상품이 아닙니다." ),
    NOT_SAME_CREATE_PRODUCT_TITLE(HttpStatus.BAD_REQUEST,"동일한 상품명으로 등록이 불가능합니다." ),
    NOT_DELETE_ON_SALE_PRODUCT(HttpStatus.BAD_REQUEST,"판매중인 상품은 삭제 할 수 없습니다." ),
    NOT_PRODUCT_PRICE_ZERO(HttpStatus.BAD_REQUEST,"상품가격이 0원일 수 없습니다."),
    INVALID_PRICE_PRODUCT(HttpStatus.BAD_REQUEST, "유효 하지 않는 상품 금액입니다.");

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
