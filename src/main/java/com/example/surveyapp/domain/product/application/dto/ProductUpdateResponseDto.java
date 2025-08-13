package com.example.surveyapp.domain.product.application.dto;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUpdateResponseDto {

    private Long id;

    private String title;

    private String content;

    private Long price;

    private String status;

    public static ProductUpdateResponseDto from(Product product) {
      return new ProductUpdateResponseDto(
              product.getId(),
              product.getTitle(),
              product.getContent(),
              product.getPrice().getValue(),
              product.getStatus().getStatus()
      );

    }
}
