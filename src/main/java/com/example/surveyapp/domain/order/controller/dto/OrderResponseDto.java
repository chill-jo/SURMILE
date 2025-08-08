package com.example.surveyapp.domain.order.controller.dto;

import com.example.surveyapp.domain.order.model.Order;
import com.example.surveyapp.domain.order.model.OrderItem;
import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderResponseDto {

    private Long orderId;

    private String orderNumber;

    private Long userId;

    private String username;

    private Long productId;

    private String title;

    private Long price;

    private String status;

    private LocalDateTime createAt;


    public static OrderResponseDto from(Order order, String username, String status) {
        OrderItem orderItem = order.getOneOrderItemOrThrow();
        return new OrderResponseDto(
            order.getId(),
            order.getOrderNumber().getValue(),
            order.getUserId(),
            username,
            orderItem.getProductId(),
            orderItem.getTitle(),
            orderItem.getPrice(),
            status,
            order.getCreatedAt()

    );

    }



}
