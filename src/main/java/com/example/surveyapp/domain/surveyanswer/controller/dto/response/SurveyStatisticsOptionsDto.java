package com.example.surveyapp.domain.surveyanswer.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 설문 통계 depth 3
 */
@Getter
@AllArgsConstructor
public class SurveyStatisticsOptionsDto {

    private final Long optionId;
    private final Long number;
    private final String content;
    private final Long count;

    public static SurveyStatisticsOptionsDto of(
            Long optionId,
            Long number,
            String content,
            Long count
    ){
        return new SurveyStatisticsOptionsDto(optionId, number, content, count);
    }
}
