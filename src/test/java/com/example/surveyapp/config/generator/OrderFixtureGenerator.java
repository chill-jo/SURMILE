package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;

import java.util.List;


public class OrderFixtureGenerator {


    public static Order generateOrderFixture(Long userId) {
        OrderItem item = OrderItemFixtureGenerator.generatorOrderItemFixture();

        Order order = Order.of(userId, List.of(item));

        return order;

    }
}
