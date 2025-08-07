package com.example.surveyapp.domain.order.model;

import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @JoinColumn(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public void delete(){
        this.isDeleted = true;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Order(OrderNumber orderNumber, Long userId, Long productId, String title, Long price) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.productId = productId;
        this.title = title;
        this.price = price;
    }

    public static Order create(Long userId, Long productId, String title, Long price) {

        return Order.builder()
                .userId(userId)
                .orderNumber(OrderNumber.generator())
                .productId(productId)
                .title(title)
                .price(price)
                .build();
    }



}
