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
public class PointBalance {

    @Column(name = "point_balance", nullable = false)
    private Long value;

    public PointBalance(Long value) {
        this.value = value;
    }

    public Long validatePoint(Long value){
        if (value == null || value < 0) {
            throw new CustomException(ErrorCode.POINT_INVALID_AMOUNT);
        }
        return value;
    }

    public PointBalance add(Long amount){
        return new PointBalance(this.value + amount);
    }

    public PointBalance minus(Long amount) {
        Long validAmount = validatePoint(amount);
        if (this.value < validAmount){
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
        }
        return new PointBalance(this.value - validAmount);
    }

}
