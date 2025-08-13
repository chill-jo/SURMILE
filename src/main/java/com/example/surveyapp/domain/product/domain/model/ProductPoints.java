package com.example.surveyapp.domain.product.domain.model;

import com.example.surveyapp.domain.product.exception.ProductErrorCode;
import com.example.surveyapp.domain.product.exception.ProductException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPoints {

    @Column(name = "products_points_amount", nullable = false)
    private Long value;

    private ProductPoints(Long value) {
        this.value = validatePriceOrThrow(value);
    }

    public static ProductPoints of(Long value) {
        return new ProductPoints(value);
    }

    public boolean isZero() {
      return this.value == 0L;
        }

    private Long validatePriceOrThrow(Long value) {
        if (value == null || value < 0) {
            throw new ProductException(ProductErrorCode.INVALID_PRICE_PRODUCT);
        }
        return value;
    }
}


