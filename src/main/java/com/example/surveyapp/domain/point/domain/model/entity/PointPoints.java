package com.example.surveyapp.domain.point.domain.model.entity;

import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
<<<<<<<< HEAD:src/main/java/com/example/surveyapp/domain/point/domain/model/entity/Points.java
public class Points {
========
public class PointPoints {
>>>>>>>> dev-v2-DDD:src/main/java/com/example/surveyapp/domain/point/domain/model/entity/PointPoints.java

    @Column(name = "point_balance", nullable = false)
    private Long value;

<<<<<<<< HEAD:src/main/java/com/example/surveyapp/domain/point/domain/model/entity/Points.java
    public Points(Long value) {
========

    private PointPoints(Long value) {
>>>>>>>> dev-v2-DDD:src/main/java/com/example/surveyapp/domain/point/domain/model/entity/PointPoints.java
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

<<<<<<<< HEAD:src/main/java/com/example/surveyapp/domain/point/domain/model/entity/Points.java
    public Points add(Points amount){
        return new Points(this.value + amount.value);
    }

    public Points minus(Points amount) {
        Long validAmount = validatePoint(amount.getValue());
        if (this.value < validAmount){
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
        }
        return new Points(this.value - validAmount);
========
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
>>>>>>>> dev-v2-DDD:src/main/java/com/example/surveyapp/domain/point/domain/model/entity/PointPoints.java
    }

}
