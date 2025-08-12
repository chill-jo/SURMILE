package com.example.surveyapp.domain.order.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateRequestDto {

    @NotNull
    private Long productId;

 }
