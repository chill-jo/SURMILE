package com.example.surveyapp.domain.survey.domain.model.vo;

import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyPoints {
    private Long value;

    private SurveyPoints(Long value){
        this.value = validatePriceOrThrow(value);
    }

    public static SurveyPoints of(Long value){
        return new SurveyPoints(value);
    }

    private Long validatePriceOrThrow(Long value) {
        if (value == null || value < 0) {
            throw new CustomException(ErrorCode.INVALID_SURVEY_POINT);
        }
        return value;
    }
}
