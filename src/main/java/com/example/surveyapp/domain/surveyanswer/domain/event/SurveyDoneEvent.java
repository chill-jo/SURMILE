package com.example.surveyapp.domain.surveyanswer.domain.event;

import lombok.Getter;

@Getter
public class SurveyDoneEvent {
    private Long surveyId;

    public SurveyDoneEvent(Long surveyId) {
        this.surveyId = surveyId;
    }
}
