package com.example.surveyapp.domain.survey.controller.dto.request;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyStatusUpdateRequestDto {

    @NotNull
    private SurveyStatus status;

    public SurveyStatusUpdateRequestDto(SurveyStatus status){
        this.status = status;
    }
}
