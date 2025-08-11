package com.example.surveyapp.domain.surveyanswer.controller.dto.response;

import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
