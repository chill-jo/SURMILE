package com.example.surveyapp.domain.payment.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointChargeRequestDto {

    @NotNull
    @Min(value=5000, message="최소 충전 금액은 5,000원 이상이어야 합니다.")
    private Long price;

}
