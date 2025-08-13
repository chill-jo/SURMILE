package com.example.surveyapp.domain.product.presentation.dto;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductStatusUpdateResponseDto {

    private Status newStatus;

    public static ProductStatusUpdateResponseDto from(Product product){
        return new ProductStatusUpdateResponseDto(product.getStatus());
    }

}
