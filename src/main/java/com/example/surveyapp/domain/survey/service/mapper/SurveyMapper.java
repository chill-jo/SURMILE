package com.example.surveyapp.domain.survey.domain;

import com.example.surveyapp.domain.survey.controller.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.SurveyResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    SurveyInfo toSurveyInfo(SurveyCreateRequestDto requestDto);
    SurveyResponseDto toResponseDto(Survey survey);

//    @BeanMapping(ignoreByDefault = true)
//    default SurveyInfo toSurveyInfo(SurveyCreateRequestDto requestDto){
//        return new SurveyInfo(
//                requestDto.getTitle(),
//                requestDto.getDescription(),
//                requestDto.getMaxSurveyee(),
//                requestDto.getPointPerPerson(),
//                requestDto.getDeadline(),
//                requestDto.getExpectedTime()
//        );
//    }

}
