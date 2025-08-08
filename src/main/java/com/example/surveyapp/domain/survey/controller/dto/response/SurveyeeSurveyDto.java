package com.example.surveyapp.domain.survey.controller.dto.response;

import com.example.surveyapp.domain.survey.domain.model.entity.SurveyAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class SurveyeeSurveyDto {

    private Long surveyId;

    private String title;

    private LocalDateTime date;

    public static SurveyeeSurveyDto of(SurveyAnswer surveyAnswer){
        return SurveyeeSurveyDto.builder()
                .surveyId(surveyAnswer.getSurveyId())
                .title(surveyAnswer.getSurveyId().getTitle())
                .date(surveyAnswer.getCreatedAt())
                .build();
    }
}
