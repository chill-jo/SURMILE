package com.example.surveyapp.domain.surveyanswer.controller.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyeeSurveyListDto {

    private final List<SurveyeeSurveyDto> survey = new ArrayList<>();

    public void addSurveyeeSurveyDto(SurveyeeSurveyDto surveyeeSurveyDto){
        this.survey.add(surveyeeSurveyDto);
    }

    public static SurveyeeSurveyListDto of(){
        return new SurveyeeSurveyListDto();
    }
}
