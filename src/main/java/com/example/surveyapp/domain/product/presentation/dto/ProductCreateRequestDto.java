package com.example.surveyapp.domain.product.presentation.dto;

import com.example.surveyapp.domain.product.domain.model.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCreateRequestDto {

    @NotNull
    @Size(max = 30)
    private final String title;

    @NotNull
    private final String content;

    @NotNull
    private final Long price;

    @NotNull
    private final Status status;
}
