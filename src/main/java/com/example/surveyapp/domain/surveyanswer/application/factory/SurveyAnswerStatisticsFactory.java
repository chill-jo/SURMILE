package com.example.surveyapp.domain.surveyanswer.application.factory;

import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyStatisticsDto;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyStatisticsOptionsDto;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyStatisticsQuestionDto;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyOptionsAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyAnswerStatisticsFactory {

    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyOptionsAnswerRepository surveyOptionsAnswerRepository;

    public SurveyStatisticsDto toStatisticsDto(Survey survey){
        return SurveyStatisticsDto.of(
                survey.getId(),
                survey.getSurveyInfo().getTitle(),
                survey.getSurveyInfo().getDescription(),
                survey.getSurveyInfo().getMaxSurveyee(),
                survey.getSurveyInfo().getPointPerPerson(),
                survey.getSurveyInfo().getTotalPoint(),
                survey.getSurveyInfo().getDeadline(),
                survey.getSurveyInfo().getExpectedTime(),
                surveyAnswerRepository.countBySurveyId(survey.getId())
        );
    }

    public List<SurveyStatisticsQuestionDto> getQuestionsDtoList(List<Question> questionList){
        List<SurveyStatisticsQuestionDto> questionDtoList = new ArrayList<>();

        questionList.forEach(question -> {
            SurveyStatisticsQuestionDto statisticsQuestionDto =
                    SurveyStatisticsQuestionDto.of(
                            question.getId(),
                            question.getNumber(),
                            question.getContent()
                    );

            statisticsQuestionDto.addOptionsDtoList(getOptionsDtoList(question));
            //질문 dto 추가
            questionDtoList.add(statisticsQuestionDto);
        });

        return questionDtoList;
    }
    public List<SurveyStatisticsOptionsDto> getOptionsDtoList(Question question){
        List<Options> options = question.getOptions();
        List<SurveyStatisticsOptionsDto> optionsDtoList = new ArrayList<>();

        if(options == null){
            return null;
        }
        options.forEach(option -> {
            Long count = surveyOptionsAnswerRepository.countByQuestionAndNumber(question, option.getNumber());
            SurveyStatisticsOptionsDto statisticsOptionsDto =
                    SurveyStatisticsOptionsDto.of(
                            option.getId(),
                            option.getNumber(),
                            option.getContent(),
                            count
                    );
            optionsDtoList.add(statisticsOptionsDto);
        });

        return optionsDtoList;
    }
}
