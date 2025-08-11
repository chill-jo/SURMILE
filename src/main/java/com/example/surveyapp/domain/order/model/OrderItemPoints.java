package com.example.surveyapp.domain.order.model;

import com.example.surveyapp.domain.product.domain.model.ProductPoints;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemPoints {
    @Column(name = "order_items_points_amount", nullable = false)
    private Long value;

    private OrderItemPoints(Long value) {
        this.value = validatePriceOrThrow(value);
    }

    public static OrderItemPoints create(Long value) {
        return new OrderItemPoints(value);
    }

    public OrderItemPoints add(OrderItemPoints amount) {
        return new OrderItemPoints(this.value + amount.value);
    }

    public OrderItemPoints minus(OrderItemPoints amount) {
        OrderItemPoints points = new OrderItemPoints(this.value - amount.value);
        return points;
    }

    public boolean isZero() {
        return this.value == 0L;
    }

    private Long validatePriceOrThrow(Long value) {
        if (value == null || value < 0) {
            throw new CustomException(ErrorCode.INVALID_PRICE_ORDERITEM);
        }
        return value;
    }
}