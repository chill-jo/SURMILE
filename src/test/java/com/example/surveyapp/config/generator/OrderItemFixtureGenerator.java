package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.order.model.OrderItem;
import com.example.surveyapp.domain.order.model.OrderItemPoints;

public class OrderItemFixtureGenerator {

    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_TITLE = "chicken";
    private static final OrderItemPoints PRODUCT_PRICE = OrderItemPoints.create(2500L);


    public static OrderItem generatorOrderItemFixture(){

        OrderItem item = OrderItem.create(PRODUCT_ID,
                PRODUCT_TITLE,
                PRODUCT_PRICE);
        return item;
    }

}
