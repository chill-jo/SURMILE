package com.example.surveyapp.domain.order.exception;

import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    //주문
    NOT_ORDER_USER(HttpStatus.UNAUTHORIZED,"주문이 불가한 계정입니다."),
    NOT_FOUND_POINT(HttpStatus.UNAUTHORIZED,"포인트가 존재하지 않습니다."),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST,"포인트가 부족합니다."),
    NOT_SURVEYEE_USER(HttpStatus.UNAUTHORIZED,"참여자 계정만 주문이 가능합니다." ),
    NOT_YOUR_ACCOUNT(HttpStatus.BAD_REQUEST,"본인이 아닌 다른 계정 조회는 불가능합니다." ),
    NOT_FOUND_ORDER(HttpStatus.UNAUTHORIZED,"해당 주문을 찾을 수 없습니다." ),
    NOT_YOUR_ORDER(HttpStatus.UNAUTHORIZED,"본인 주문만 확인 할 수 있습니다." ),
    NOT_SAME_ORDER_USER(HttpStatus.BAD_REQUEST,"본인이 주문한 주문만 삭제할 수 있습니다." ),
    ONE_ORDER_ONE_PRODUCT(HttpStatus.BAD_REQUEST, "한개의 상품만 있어야 주문이 가능합니다." ),
    INVALID_PRICE_ORDERITEM(HttpStatus.BAD_REQUEST, "유효 하지 않는 상품 금액입니다." );

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
