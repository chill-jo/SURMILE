package com.example.surveyapp.domain.payment.presentation.dto.response;

import com.example.surveyapp.domain.payment.domain.model.enums.Method;
import com.example.surveyapp.domain.payment.domain.model.enums.PointStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointChargeResponseDto {
    private final Long paymentId;
    private final Long amount;
    private final PointStatus status;
    private final Method method;
}
