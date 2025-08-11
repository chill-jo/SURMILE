package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.order.model.Order;
import com.example.surveyapp.domain.order.model.OrderItem;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.user.domain.model.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;


public class OrderFixtureGenerator {


    public static Order generateOrderFixture(Long userId) {
        OrderItem item = OrderItemFixtureGenerator.generatorOrderItemFixture();

        Order order = Order.create(userId, List.of(item));

        return order;

    }
}
