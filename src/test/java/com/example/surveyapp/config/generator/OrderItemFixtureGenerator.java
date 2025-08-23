package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;
import com.example.surveyapp.domain.order.domain.model.vo.OrderItemPoints;

public class OrderItemFixtureGenerator {

    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_TITLE = "chicken";
    private static final OrderItemPoints PRODUCT_PRICE = OrderItemPoints.of(2500L);

    public static OrderItem generatorOrderItemFixture(){

        OrderItem item = OrderItem.of(PRODUCT_ID,
                PRODUCT_TITLE,
                PRODUCT_PRICE);
        return item;
    }

}
