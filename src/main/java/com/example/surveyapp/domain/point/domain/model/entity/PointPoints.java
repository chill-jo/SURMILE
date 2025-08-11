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
public class PointPoints {

    @Column(name = "point_balance", nullable = false)
    private Long value;

    private PointPoints(Long value) {
        this.value = value;
    }

    public Long validatePoint(Long value){
        if (value == null || value < 0) {
            throw new CustomException(ErrorCode.POINT_INVALID_AMOUNT);
        }
        return value;
    }

    public static PointPoints create(Long value) {
        return new PointPoints(value);
    }

    public PointPoints add(PointPoints amount){
        validatePoint(amount.getValue());
        return new PointPoints(this.value + amount.value);
    }

    public PointPoints minus(PointPoints amount) {
        validatePoint(amount.getValue());
        if (this.value < amount.value){
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
        }
        return new PointPoints(this.value - amount.value
        );
    }

}
