package com.example.surveyapp.domain.point.domain.model.entity;


import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity

public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long currentBalance;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long afterBalance;

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
    @JoinColumn(name = "point_id")
    private Point point;


    @Builder(access = AccessLevel.PRIVATE)
    private PointHistory (Long currentBalance, Long amount, Long afterBalance, PointType type, Target target, Long targetId, String description, Long userId, Point point){
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

    public static PointHistory of(Long currentBalance, Long amount, Long afterBalance, PointType type, Target target, Long targetId, String description, Long userId, Point point){
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
