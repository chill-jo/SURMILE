package com.example.surveyapp.domain.user.presentation.dto;

import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseDataInfoResponseDto {

    private final String question_1 = CategoryEnum.GENDER.getCategory();
    private final String option_1 = CategoryEnum.GENDER.getDescription();

    private final String question_2 = CategoryEnum.AGE.getCategory();
    private final String option_2 = CategoryEnum.AGE.getDescription();

    private final String question_3 = CategoryEnum.REGIDENCE.getCategory();
    private final String option_3 = CategoryEnum.REGIDENCE.getDescription();

}
