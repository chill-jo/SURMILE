package com.example.surveyapp.domain.survey.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyCreateRequestDto {

    @NotBlank(message = "설문 제목은 필수입니다.")
    @Length(min = 5, max = 100, message = "설문 제목은 5~100자 사이여야 합니다.")
    private String title;

    @NotBlank(message = "설문 상세설명은 필수입니다.")
    @Length(min = 5, max = 255, message = "설문 상세설명은 5~255자 사이여야 합니다.")
    private String description;

    @NotNull(message = "설문 참여 가능 인원은 필수입니다.")
    @Min(value = 10, message = "설문 참여 가능 인원은 최소 10명입니다.")
    private Long maxSurveyee;

    @NotNull(message = "인당 지급 포인트는 필수입니다.")
    @Min(value = 10, message = "인당 지급 포인트는 최소 10포인트입니다.")
    private Long pointPerPerson;

    @NotNull(message = "설문 마감 기한은 필수입니다.")
    private LocalDateTime deadline;

    @NotNull(message = "예상 소요시간은 필수입니다.")
    @Range(min = 1, max = 120, message = "예상 소요시간은 1분 이상, 120분 이하여야 합니다.")
    private Long expectedTime;
}
