package com.example.surveyapp.domain.point.domain.model.entity.vo;

import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointBalance {

    private Long value;

    private PointBalance(Long value) {
        this.value = validatePoint(value);
    }

    public static PointBalance of(Long value){
        return new PointBalance(value);
    }

    public Long validatePoint(Long value){
        if (value == null || value < 0) {
            throw new PointException(PointErrorCode.POINT_INVALID_AMOUNT);
        }
        return value;
    }
}
