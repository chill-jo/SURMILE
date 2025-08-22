package com.example.surveyapp.domain.payment.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class PaymentException extends BaseException {
    public PaymentException(ErrorCode errorCode) {

        super(errorCode);
    }
}
