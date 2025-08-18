package com.example.surveyapp.domain.point.domain.event;

import lombok.Getter;

@Getter
public class PointRedeemFailEvent {
    private final Long userId;
    private final Long surveyId;

    public PointRedeemFailEvent(Long userId, Long surveyId){
        this.userId = userId;
        this.surveyId = surveyId;
    }
}
