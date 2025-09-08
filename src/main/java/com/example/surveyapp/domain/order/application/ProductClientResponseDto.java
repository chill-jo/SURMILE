package com.example.surveyapp.domain.order.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductClientResponseDto {
    private Long id;

    private String title;

    private Long price;

    private String status;

}
