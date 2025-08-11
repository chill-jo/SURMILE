package com.example.surveyapp.domain.surveyanswer.controller.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 유저 설문 참여 히스토리 목록 depth 1
 */
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
