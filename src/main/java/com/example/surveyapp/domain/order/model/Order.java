package com.example.surveyapp.domain.order.model;

import com.example.surveyapp.global.config.entity.BaseEntity;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OrderNumber orderNumber;

   @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private final List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private boolean isDeleted = false;

    public OrderItem onlyOneOrderItem(){
        if (orderItems.size() != 1) {
            throw new CustomException(ErrorCode.ONE_ORDER_ONE_PRODUCT);
        }
        return orderItems.stream().findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.ONE_ORDER_ONE_PRODUCT));
    }

    public void delete(){
        this.isDeleted = true;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Order(OrderNumber orderNumber, Long userId, List<OrderItem> orderItems) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.orderItems.addAll(orderItems);
    }

    public static Order create(Long userId, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.size() != 1){
            throw new CustomException(ErrorCode.ONE_ORDER_ONE_PRODUCT);
        }
        return Order.builder()
                .userId(userId)
                .orderNumber(OrderNumber.generator())
                .orderItems(orderItems)
                .build();
    }

}
