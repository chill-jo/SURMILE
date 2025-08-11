package com.example.surveyapp.domain.point.domain.model.entity;

import com.example.surveyapp.global.config.entity.BaseEntity;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    private Points points;

    @Builder(access = AccessLevel.PRIVATE)
    private Point(Long userId){
        this.userId=userId;
        this.points = new Points(0L);
    }

    public static Point of(Long userId){
        return Point.builder()
                .userId(userId)
                .build();
    }

    public void pointCharge(Points amount) {
        if(amount.getValue() == null || amount.getValue() < 5000){
            throw new CustomException(ErrorCode.POINT_INVALID_AMOUNT);
        }
        this.points = this.points.add(amount);
    }

    public void earn(Points amount) {
        this.points = points.add(amount);
    }

    public void redeem(Points amount){
        this.points = points.minus(amount);
    }

}
