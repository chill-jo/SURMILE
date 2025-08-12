package com.example.surveyapp.domain.point.domain.model.entity;

import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name= "point_wallet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointWallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name="point_balance", nullable = false))
    private PointBalance pointBalance;

    @Builder(access = AccessLevel.PRIVATE)
    private PointWallet(Long userId, PointBalance pointBalance){
        this.userId=userId;
        this.pointBalance = pointBalance;
    }

    public static PointWallet of(Long userId){
        return new PointWallet(userId, PointBalance.of(0L));
    }

    public void pointCharge(PointBalance amount) {
        if(amount == null || amount.getValue() < 5000){
            throw new PointException(PointErrorCode.POINT_INVALID_AMOUNT);
        }

        pointBalance = PointBalance.of(pointBalance.getValue() + amount.getValue());
    }

    public void earn(PointBalance amount) {

        pointBalance = PointBalance.of(pointBalance.getValue() + amount.getValue());
    }

    public void redeem(PointBalance amount){

        pointBalance = PointBalance.of(pointBalance.getValue() - amount.getValue());
    }

}
