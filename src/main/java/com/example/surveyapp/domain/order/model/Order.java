package com.example.surveyapp.domain.order.model;

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

   @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

   @OneToMany(cascade ={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private final List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private boolean isDeleted = false;

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

        return Order.builder()
                .userId(userId)
                .orderNumber(OrderNumber.generator())
                .orderItems(orderItems)
                .build();
    }



}
