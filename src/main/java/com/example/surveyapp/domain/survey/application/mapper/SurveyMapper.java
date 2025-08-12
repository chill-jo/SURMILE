package com.example.surveyapp.domain.survey.application.mapper;

import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.QuestionOptionsDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyQuestionDto;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyPoints;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SurveyMapper {

    private final SurveyQuestionService surveyQuestionService = new SurveyQuestionService();

    // survey 상세조회
    public SurveyResponseDto toResponseDto(Survey survey){
        SurveyInfo info = survey.getSurveyInfo();

        return new SurveyResponseDto(
                survey.getId(),
                info.getTitle(),
                info.getDescription(),
                info.getMaxSurveyee(),
                info.getPointPerPerson().getValue(),
                info.getTotalPoint().getValue(),
                info.getDeadline(),
                info.getExpectedTime(),
                survey.getStatus(),
                null
        );
    }

    //설문 시작 시 설문상세정보,질문,선택지 모아보기
   public SurveyQuestionDto toSurveyQuestionDto(Survey survey) {
        SurveyQuestionDto surveyQuestionDto = SurveyQuestionDto.of(survey);

        List<Question> questions = surveyQuestionService.getQuestionsSortedByNumber(survey);
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

    //설문생성요청 -> survey info VO
    public SurveyInfo toSurveyInfo(SurveyCreateRequestDto requestDto){
        return new SurveyInfo(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getMaxSurveyee(),
                SurveyPoints.of(requestDto.getPointPerPerson()),
                requestDto.getDeadline(),
                requestDto.getExpectedTime()
        );
    }

}
