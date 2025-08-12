package com.example.surveyapp.domain.order.domain.model;

import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;
import com.example.surveyapp.domain.order.domain.model.vo.OrderNumber;
import com.example.surveyapp.domain.order.exception.OrderErrorCode;
import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.global.config.entity.BaseEntity;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private final List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private boolean isDeleted = false;

    //총 금액 구하기 (1:1 주문)
    public Long orderAmount() {
        return getOneOrderItemOrThrow().getPrice().getValue();
    }

    //수량 1개만 주문 가능 외 예외처리
    public OrderItem getOneOrderItemOrThrow(){
        if (orderItems.size() != 1) {
            throw new OrderException(OrderErrorCode.ONE_ORDER_ONE_PRODUCT);
        }
        return orderItems.stream().findFirst()
                .orElseThrow(() -> new OrderException(OrderErrorCode.ONE_ORDER_ONE_PRODUCT));
    }

    //본인 주문 외 조회 시 예외처리
    public void validateOrderer(Long userId){
        if (!this.getUserId().equals(userId)){
            throw new OrderException(OrderErrorCode.NOT_YOUR_ORDER);
        }
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

    public static Order of(Long userId, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.size() != 1){
            throw new OrderException(OrderErrorCode.ONE_ORDER_ONE_PRODUCT);
        }
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(OrderNumber.generator())
                .orderItems(orderItems)
                .build();
        return order;
    }

}
