package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SurveyResponseDto {
    private final Long id;

    private final String title;

    private final String description;

    private final Long maxSurveyee;

    private final Long pointPerPerson;

    private final Long totalPoint;

    private final LocalDateTime deadline;

    private final Long expectedTime;

    private final SurveyStatus status;

    private Long surveyeeCount = 0L;

    public void changeSurveyeeCount(Long surveyeeCount) {
        this.surveyeeCount = surveyeeCount;
    }

}
