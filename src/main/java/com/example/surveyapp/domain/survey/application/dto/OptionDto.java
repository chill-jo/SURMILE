package com.example.surveyapp.domain.survey.application.dto;

import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptionDto {
    private final Long optionId;
    private final Long number;
    private final String content;

    public static OptionDto of(Long optionId, Long number, String content){
        return new OptionDto(optionId, number, content);
    }
    public static OptionDto from(Options option){
        return new OptionDto(option.getId(), option.getNumber(), option.getContent());
    }
}
