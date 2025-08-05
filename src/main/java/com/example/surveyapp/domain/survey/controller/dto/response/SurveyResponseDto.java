package com.example.surveyapp.domain.survey.controller.dto.response;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SurveyResponseDto {
    private final Long id;

    private String title;

    private String description;

    private Long maxSurveyee;

    private Long pointPerPerson;

    private Long totalPoint;

    private LocalDateTime deadline;

    private Long expectedTime;

    private SurveyStatus status;

    private Long surveyeeCount = 0L;

    public void changeSurveyeeCount(Long surveyeeCount) {
        this.surveyeeCount = surveyeeCount;
    }

}
