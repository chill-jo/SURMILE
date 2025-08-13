package com.example.surveyapp.domain.survey.domain.event;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SurveyCreateEvent {
    private final Survey survey;
    private final Long userId;
}
