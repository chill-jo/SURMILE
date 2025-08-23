package com.example.surveyapp.domain.product.exception;

import com.example.surveyapp.global.response.exception.BaseException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public class ProductException extends BaseException {

    public ProductException(ErrorCode errorCode) {

      super(errorCode);
    }
}
