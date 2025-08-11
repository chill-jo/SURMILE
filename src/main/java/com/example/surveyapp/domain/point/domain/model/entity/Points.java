package com.example.surveyapp.domain.point.domain.model.entity;

import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Points {

    @Column(name = "point_balance", nullable = false)
    private Long value;

    public Points(Long value) {
        this.value = value;
    }

    public static Points of(Long value){
        return new Points(value);
    }

    public Long validatePoint(Long value){
        if (value == null || value < 0) {
            throw new CustomException(ErrorCode.POINT_INVALID_AMOUNT);
        }
        return value;
    }

    public Points add(Points amount){
        return new Points(this.value + amount.value);
    }

    public Points minus(Points amount) {
        Long validAmount = validatePoint(amount.getValue());
        if (this.value < validAmount){
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
        }
        return new Points(this.value - validAmount);
    }

}
