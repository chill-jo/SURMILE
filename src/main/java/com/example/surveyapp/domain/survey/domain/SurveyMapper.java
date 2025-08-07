package com.example.surveyapp.domain.survey.controller.dto;

import com.example.surveyapp.domain.survey.controller.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.SurveyUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.SurveyResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.user.domain.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    @Mapping(target = "surveyeeCount", ignore = true)
    SurveyResponseDto toResponseDto(Survey survey);

    @BeanMapping(ignoreByDefault = true)
    default Survey createSurveyEntity(SurveyCreateRequestDto dto, Long userId){
        return new Survey(
                userId,
                dto.getTitle(),
                dto.getDescription(),
                dto.getMaxSurveyee(),
                dto.getPointPerPerson(),
                dto.getDeadline(),
                dto.getExpectedTime()
        );
    }

    @BeanMapping(ignoreByDefault = true)
    default void updateSurvey(SurveyUpdateRequestDto dto, @MappingTarget Survey survey){
        survey.update(
                dto.getTitle(),
                dto.getDescription(),
                dto.getMaxSurveyee(),
                dto.getPointPerPerson(),
                dto.getDeadline(),
                dto.getExpectedTime()
        );
    }



}
