package com.example.surveyapp.domain.surveyanswer.application.mapper;

import com.example.surveyapp.domain.survey.application.SurveyQueryService;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyeeSurveyDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyeeSurveyListDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyAnswerMapper {

    private final SurveyQueryService surveyQuestionQueryService;

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

