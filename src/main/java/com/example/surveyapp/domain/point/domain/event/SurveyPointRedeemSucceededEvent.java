package com.example.surveyapp.domain.point.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyPointRedeemSucceededEvent {
    private Long userId;
    private Long surveyId;
}
