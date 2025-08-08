package com.example.surveyapp.domain.survey.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SurveyUpdateRequestDto {

    @Length(min = 5, max = 50, message = "설문 제목은 5~50자 사이여야 합니다.")
    private String title;

    @Length(min = 5, max = 255, message = "설문 상세설명은 5~255자 사이여야 합니다.")
    private String description;

    @Min(value = 10, message = "설문 참여 가능 인원은 최소 10명입니다.")
    private Long maxSurveyee;

    @Min(value = 10, message = "인당 지급 포인트는 최소 10포인트입니다.")
    private Long pointPerPerson;

    private LocalDateTime deadline;

    @Range(min = 1, max = 120, message = "예상 소요시간은 1분 이상, 120분 이하여야 합니다.")
    private Long expectedTime;

}

