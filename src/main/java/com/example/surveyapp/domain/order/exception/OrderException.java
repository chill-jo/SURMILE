package com.example.surveyapp.domain.order.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class OrderException extends BaseException {

    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
