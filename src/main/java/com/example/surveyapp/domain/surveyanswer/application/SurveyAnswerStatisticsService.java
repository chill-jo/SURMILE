package com.example.surveyapp.domain.surveyanswer.application;

import com.example.surveyapp.domain.survey.application.SurveyQueryService;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.domain.surveyanswer.application.factory.SurveyAnswerStatisticsFactory;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyStatisticsDto;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyStatisticsQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyAnswerStatisticsService {

    private final SurveyQueryService surveyQuestionQueryService;
    private final SurveyQuestionService surveyQuestionService;
    private final SurveyAnswerStatisticsFactory surveyStatisticsFactory;

    @Transactional
    public SurveyStatisticsDto getStatistics(Long surveyId){
        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);

        SurveyStatisticsDto surveyStatisticsDto = surveyStatisticsFactory.toStatisticsDto(survey);

        List<Question> questionList = surveyQuestionService.getQuestionsSortedByNumber(survey);
        List<SurveyStatisticsQuestionDto> questionDtoList = surveyStatisticsFactory.getQuestionsDtoList(questionList);

        surveyStatisticsDto.addQuestionDtoList(questionDtoList);

        return surveyStatisticsDto;
    }
}
