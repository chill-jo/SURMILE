package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import lombok.Getter;

@Getter
public class SurveyStatusResponseDto {
    private final SurveyStatus status;

    public SurveyStatusResponseDto(SurveyStatus status){
        this.status = status;
    }
}
