package com.example.surveyapp.domain.survey.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SurveyAnswerEvent {
    private final Long userId;
    private final Long surveyId;
    private final Long surveyAnswerId;
}
