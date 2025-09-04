package com.example.surveyapp.domain.surveyanswer.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAnswerEvent {
    private Long userId;
    private Long pointPerPerson;
    private Long surveyAnswerId;
}
