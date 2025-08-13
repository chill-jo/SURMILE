package com.example.surveyapp.domain.product.application.dto;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ProductUpdateResponseDto {

    private final Long id;

    private final String title;

    private final String content;

    private final Long price;

    private final Status status;

    public static ProductUpdateResponseDto from(Product product) {
      return new ProductUpdateResponseDto(
              product.getId(),
              product.getTitle(),
              product.getContent(),
              product.getPrice().getValue(),
              product.getStatus()
      );

    }
}
