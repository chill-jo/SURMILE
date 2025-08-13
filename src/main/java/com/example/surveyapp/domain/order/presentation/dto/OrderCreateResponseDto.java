package com.example.surveyapp.domain.order.presentation.dto;

import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateResponseDto {

    private Long id;

    private String orderNumber;

    private String title;

    private String status;

    private Long price;

    public static OrderCreateResponseDto from(Order order, String status) {
        OrderItem orderItem = order.getOneOrderItemOrThrow();
        return new OrderCreateResponseDto(
                order.getUserId(),
                order.getOrderNumber().getValue(),
                orderItem.getTitle(),
                status,
                order.orderAmount());
    }
}
