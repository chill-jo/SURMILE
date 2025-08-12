package com.example.surveyapp.domain.survey.presentation.dto.request;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SurveyStatusUpdateRequestDto {

    @NotNull
    private final SurveyStatus status;

    public SurveyStatusUpdateRequestDto(SurveyStatus status){
        this.status = status;
    }
}
