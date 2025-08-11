package com.example.surveyapp.domain.surveyanswer.event;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SurveyAnswerEvent {
    private final Long userId;
    private final Survey survey;
    private final Long surveyAnswerId;
}
