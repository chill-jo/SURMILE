package com.example.surveyapp.domain.surveyanswer.application.factory;

import com.example.surveyapp.domain.survey.application.SurveyQuestionQueryService;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyeeSurveyDto;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyeeSurveyListDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyAnswerFactory {

    private final SurveyQuestionQueryService surveyQuestionQueryService;

    public SurveyeeSurveyListDto createParticipatedSurveyListDto(List<SurveyAnswer> surveyAnswerList){

        SurveyeeSurveyListDto surveyListDto = SurveyeeSurveyListDto.of();

        surveyAnswerList.forEach(surveyAnswer -> {
            String title = surveyQuestionQueryService
                    .findSurvey(surveyAnswer.getSurveyId())
                    .getSurveyInfo()
                    .getTitle();

            SurveyeeSurveyDto surveyeeSurveyDto = SurveyeeSurveyDto.of(
                    surveyAnswer.getSurveyId(),
                    title,
                    surveyAnswer.getCreatedAt()
            );
            surveyListDto.addSurveyeeSurveyDto(surveyeeSurveyDto);
        });

        return surveyListDto;
    }
}

