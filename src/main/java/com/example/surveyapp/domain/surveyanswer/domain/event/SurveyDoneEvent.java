package com.example.surveyapp.domain.surveyanswer.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDoneEvent {
    private Long surveyId;
}
