package com.example.surveyapp.domain.product.controller.dto;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;

    private String title;

    private Long price;

    private Status status;

    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getTitle(),
                product.getPrice().getValue(),
                product.getStatus()
        );
    }
}


