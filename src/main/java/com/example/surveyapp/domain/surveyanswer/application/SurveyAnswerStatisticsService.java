package com.example.surveyapp.domain.surveyanswer.application;

import com.example.surveyapp.domain.survey.application.dto.QuestionDto;
import com.example.surveyapp.domain.surveyanswer.application.facade.SurveyFacade;
import com.example.surveyapp.domain.surveyanswer.application.mapper.SurveyAnswerStatisticsMapper;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyStatisticsDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyStatisticsQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyAnswerStatisticsService {

    private final SurveyAnswerStatisticsMapper surveyStatisticsFactory;
    private final SurveyFacade surveyFacade;

    @Transactional
    public SurveyStatisticsDto getStatistics(Long surveyId){

        SurveyStatisticsDto surveyStatisticsDto = surveyStatisticsFactory.toStatisticsDto(surveyId);

        List<QuestionDto> questionDtoList = surveyFacade.getQuestionDtos(surveyId);
        List<SurveyStatisticsQuestionDto> statisticsQuestionDtoList = surveyStatisticsFactory.getQuestionsDtoList(surveyId, questionDtoList);

        surveyStatisticsDto.addQuestionDtoList(statisticsQuestionDtoList);

        return surveyStatisticsDto;
    }
}
