package com.example.surveyapp.domain.surveyanswer.presentation.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
/**
 * 설문 통계 depth 2
 */
@Getter
@RequiredArgsConstructor
public class SurveyStatisticsQuestionDto {

    private final Long questionId;
    private final Long number;
    private final String content;
    private List<SurveyStatisticsOptionsDto> options;

    public void addOptionsDtoList(List<SurveyStatisticsOptionsDto> optionsDtoList){
        this.options = optionsDtoList;
    }

    public static SurveyStatisticsQuestionDto of(
            Long questionId,
            Long number,
            String content
    ){
        return new SurveyStatisticsQuestionDto(questionId, number, content);
    }

}
