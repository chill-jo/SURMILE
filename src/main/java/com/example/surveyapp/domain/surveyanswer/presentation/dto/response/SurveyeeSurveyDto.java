package com.example.surveyapp.domain.surveyanswer.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 유저 설문 참여 히스토리 depth 2
 */
@Getter
@AllArgsConstructor
public class SurveyeeSurveyDto {

    private final Long surveyId;

    private String title;

    private final LocalDateTime date;

    public static SurveyeeSurveyDto of(Long surveyId, String title, LocalDateTime date){
        return new SurveyeeSurveyDto(surveyId, title, date);
    }
}
