package com.example.surveyapp.domain.user.presentation.dto;

import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BaseDataRequestDto {

    @NotNull(message = "카테고리를 선택해주세요.")
    private CategoryEnum category;

    @NotNull(message = "답을 입력해주세요.")
    private Long answer;

    @AssertTrue(message = "유효하지 않은 답변입니다.")
    private boolean isValidAnswer() {
        return category != null && answer != null && answer > 0 && answer <= category.getOptionMaxNum();
    }



}
