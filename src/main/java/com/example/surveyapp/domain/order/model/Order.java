package com.example.surveyapp.domain.order.model;

import com.example.surveyapp.domain.order.event.OrderCreateEvent;
import com.example.surveyapp.global.config.entity.BaseEntity;
import com.example.surveyapp.global.config.event.EventsPublisher;
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
            throw new CustomException(ErrorCode.ONE_ORDER_ONE_PRODUCT);
        }
        return orderItems.stream().findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.ONE_ORDER_ONE_PRODUCT));
    }

    //본인 주문 외 조회 시 예외처리
    public void validateOrderer(Long userId){
        if (!this.getUserId().equals(userId)){
            throw new CustomException(ErrorCode.NOT_YOUR_ORDER);
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

    public static Order create(Long userId, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.size() != 1){
            throw new CustomException(ErrorCode.ONE_ORDER_ONE_PRODUCT);
        }
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(OrderNumber.generator())
                .orderItems(orderItems)
                .build();
        EventsPublisher.raise(new OrderCreateEvent(order.getId(),
                userId,
                order.orderAmount())); //이벤트 발행
        return order;
    }

}
