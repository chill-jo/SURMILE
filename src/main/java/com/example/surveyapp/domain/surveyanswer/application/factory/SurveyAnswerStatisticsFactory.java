package com.example.surveyapp.domain.surveyanswer.application.factory;

import com.example.surveyapp.domain.survey.application.dto.OptionDto;
import com.example.surveyapp.domain.survey.application.dto.QuestionDto;
import com.example.surveyapp.domain.survey.application.dto.SurveyInfoDto;
import com.example.surveyapp.domain.surveyanswer.application.facade.SurveyFacade;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyStatisticsDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyStatisticsOptionsDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyStatisticsQuestionDto;
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
    private final SurveyFacade surveyFacade;

    public SurveyStatisticsDto toStatisticsDto(Long surveyId){
        SurveyInfoDto infoDto = surveyFacade.getSurveyInfo(surveyId);

        return SurveyStatisticsDto.of(
                surveyId,
                infoDto.getTitle(),
                infoDto.getDescription(),
                infoDto.getMaxSurveyee(),
                infoDto.getPointPerPerson(),
                infoDto.getTotalPoint(),
                infoDto.getDeadline(),
                infoDto.getExpectedTime(),
                surveyAnswerRepository.countBySurveyId(surveyId)
        );
    }

    public List<SurveyStatisticsQuestionDto> getQuestionsDtoList(Long surveyId, List<QuestionDto> questionDtoList){
        List<SurveyStatisticsQuestionDto> statisticsQuestionDtoList = new ArrayList<>();

        questionDtoList.forEach(questionDto -> {
            SurveyStatisticsQuestionDto statisticsQuestionDto =
                    SurveyStatisticsQuestionDto.of(
                            questionDto.getQuestionId(),
                            questionDto.getNumber(),
                            questionDto.getContent()
                    );

            statisticsQuestionDto.addOptionsDtoList(getOptionsDtoList(surveyId, questionDto.getQuestionId()));
            //질문 dto 추가
            statisticsQuestionDtoList.add(statisticsQuestionDto);
        });

        return statisticsQuestionDtoList;
    }
    public List<SurveyStatisticsOptionsDto> getOptionsDtoList(Long surveyId, Long questionId){
        List<OptionDto> options = surveyFacade.getOptionDtos(surveyId, questionId);

        List<SurveyStatisticsOptionsDto> optionsDtoList = new ArrayList<>();

        if(options == null){
            return null;
        }
        options.forEach(optionDto -> {
            Long count = surveyOptionsAnswerRepository.countByQuestionIdAndNumber(questionId, optionDto.getNumber());
            SurveyStatisticsOptionsDto statisticsOptionsDto =
                    SurveyStatisticsOptionsDto.of(
                            optionDto.getOptionId(),
                            optionDto.getNumber(),
                            optionDto.getContent(),
                            count
                    );
            optionsDtoList.add(statisticsOptionsDto);
        });

        return optionsDtoList;
    }
}
