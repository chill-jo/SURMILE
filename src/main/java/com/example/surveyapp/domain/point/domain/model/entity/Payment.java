package com.example.surveyapp.domain.point.domain.model.entity;

import com.example.surveyapp.domain.point.domain.model.entity.vo.Money;
import com.example.surveyapp.domain.point.domain.model.enums.Method;
import com.example.surveyapp.domain.point.domain.model.enums.TargetType;
import com.example.surveyapp.domain.point.domain.model.enums.PointStatus;
import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Money amount;

    @Column(unique = true, nullable = false)
    private UUID paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointStatus pointStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Method method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(Money amount, PointStatus pointStatus, Method method, TargetType targetType) {
        this.amount = amount;
        this.paymentKey = UUID.randomUUID();
        this.pointStatus = pointStatus;
        this.method = method;
        this.targetType = targetType;
    }

    public static Payment of(Money amount, PointStatus pointStatus, Method method, TargetType targetType){
        return Payment.builder()
                .amount(amount)
                .pointStatus(pointStatus)
                .method(method)
                .targetType(targetType)
                .build();
    }
}
