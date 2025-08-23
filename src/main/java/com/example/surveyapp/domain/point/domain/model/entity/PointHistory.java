package com.example.surveyapp.domain.point.domain.model.entity;

import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name="current_balance", nullable = false))
    private PointBalance currentBalance;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name="amount", nullable = false))
    private PointBalance amount;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name="after_balance", nullable = false))
    private PointBalance afterBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Target target;

    @Column(nullable = false)
    private Long targetId;

    @Column(length = 255, nullable = false)
    private String description;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_wallet_id")
    private PointWallet point;

    @Builder(access = AccessLevel.PRIVATE)
    private PointHistory (PointBalance currentBalance, PointBalance amount, PointBalance afterBalance, PointType type, Target target, Long targetId, String description, Long userId, PointWallet point){
        this.currentBalance=currentBalance;
        this.amount=amount;
        this.afterBalance=afterBalance;
        this.type=type;
        this.target=target;
        this.targetId=targetId;
        this.description=description;
        this.userId = userId;
        this.point=point;
    }

    public static PointHistory of(PointBalance currentBalance, PointBalance amount, PointBalance afterBalance, PointType type, Target target, Long targetId, String description, Long userId, PointWallet point){
        return PointHistory.builder()
                .currentBalance(currentBalance)
                .amount(amount)
                .afterBalance(afterBalance)
                .type(type)
                .target(target)
                .targetId(targetId)
                .description(description)
                .userId(userId)
                .point(point)
                .build();
    }
}
