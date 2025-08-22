package com.example.surveyapp.domain.survey.domain.event;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SurveyCreateEvent {
    private final Long surveyId;
    private final Long totalPoint;
    private final Long userId;
}
