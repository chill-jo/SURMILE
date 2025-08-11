package com.example.surveyapp.domain.survey.service.mapper;

import com.example.surveyapp.domain.survey.controller.dto.response.*;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SurveyAnswerMapper {

    public SurveyQuestionDto toSurveyQuestionDto(Survey survey){
        SurveyQuestionDto surveyQuestionDto = SurveyQuestionDto.of(survey);

        List<Question> questions = survey.getQuestions();
        questions.forEach(question -> {
            List<Options> options = question.getOptionsOrderByNumber();
            List<OptionResponseDto> optionDtos = options.stream()
                    .map(OptionResponseDto::from)
                    .collect(Collectors.toList());

            QuestionOptionsDto questionOptionsDto = QuestionOptionsDto.of(question, optionDtos);
            surveyQuestionDto.addQuestion(questionOptionsDto);
        });

        return surveyQuestionDto;
    }

    public SurveyeeSurveyListDto toSurveyListDto(List<SurveyAnswer> surveyAnswerList){

        SurveyeeSurveyListDto surveyListDto = SurveyeeSurveyListDto.of();

        surveyAnswerList.forEach(surveyAnswer -> {
            SurveyeeSurveyDto surveyeeSurveyDto = SurveyeeSurveyDto.of(surveyAnswer);

            surveyListDto.addSurveyeeSurveyDto(surveyeeSurveyDto);
        });

        return surveyListDto;
    }
}
