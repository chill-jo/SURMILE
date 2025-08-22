package com.example.surveyapp.domain.point.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveyPointRedeemFailedEvent {
    private final Long userId;
    private final Long SurveyId;
}
