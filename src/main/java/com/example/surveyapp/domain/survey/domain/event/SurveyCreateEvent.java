package com.example.surveyapp.domain.survey.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCreateEvent {
    private Long surveyId;
    private Long totalPoint;
    private Long userId;
}
