package com.example.surveyapp.domain.order.domain.model;

import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;
import com.example.surveyapp.domain.order.domain.model.vo.OrderNumber;
import com.example.surveyapp.domain.order.domain.model.vo.OrderStatus;
import com.example.surveyapp.domain.order.exception.OrderErrorCode;
import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Embedded
    private OrderItem orderItem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private boolean isDeleted = false;

    //총 금액 구하기 (1:1 주문)
    public Long orderAmount() {

        return orderItem.getPrice().getValue();
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
    private Order(OrderNumber orderNumber, Long userId, OrderItem orderItem, OrderStatus orderStatus) {

        this.orderNumber = orderNumber;
        this.userId = userId;
        this.orderItem =orderItem;
        this.orderStatus = orderStatus;
    }

    public static Order of(Long userId, OrderItem orderItem) {
        if (orderItem == null) throw new OrderException(OrderErrorCode.ONE_ORDER_ONE_PRODUCT);

        Order order = Order.builder()
                .userId(userId)
                .orderNumber(OrderNumber.generator())
                .orderItem(orderItem)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();
        return order;
    }

    public void confirm(){
        if (orderStatus != OrderStatus.PENDING_PAYMENT) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (orderStatus != OrderStatus.PENDING_PAYMENT) {
            throw new OrderException(OrderErrorCode.FAILED_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.CANCELLED;
    }
}
