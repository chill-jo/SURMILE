package com.example.surveyapp.domain.surveyanswer.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SurveyAnswerEvent {
    private final Long userId;
    private final Long pointPerPerson;
    private final Long surveyAnswerId;
}
