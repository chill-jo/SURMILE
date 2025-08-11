package com.example.surveyapp.domain.product.domain.model;

import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class ProductPoints {

    @Column(name = "products_points_amount", nullable = false)
    private Long value;

    private ProductPoints(Long value) {
        this.value = validatePriceOrThrow(value);
    }

    public static ProductPoints create(Long value) {
        return new ProductPoints(value);
    }

    public ProductPoints add(ProductPoints amount) {
        return new ProductPoints(this.value + amount.value);
    }

    public ProductPoints minus(ProductPoints amount) {
        ProductPoints points = new ProductPoints(this.value - amount.value);
        return points;
    }

    public boolean isZero() {
      return this.value == 0L;
        }

    private Long validatePriceOrThrow(Long value) {
        if (value == null || value < 0) {
            throw new CustomException(ErrorCode.INVALID_PRICE_PRODUCT);
        }
        return value;
    }
    }


