package com.example.surveyapp.domain.order.domain.model.vo;

import com.example.surveyapp.domain.order.exception.OrderErrorCode;
import com.example.surveyapp.domain.order.exception.OrderException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemPoints {
    @Column(name = "orders_item_point", nullable = false)
    private Long value;

    public static OrderItemPoints of(Long value) {
        return new OrderItemPoints(value);
    }
    private OrderItemPoints(Long value) {
        this.value = validatePriceOrThrow(value);
    }
    private Long validatePriceOrThrow(Long value) {
        if (value == null || value <= 0) {
            throw new OrderException(OrderErrorCode.INVALID_PRICE_ORDERITEM);
        }
        return value;
    }


}

