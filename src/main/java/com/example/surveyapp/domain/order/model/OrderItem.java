package com.example.surveyapp.domain.order.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    /**
     * 주문 당시 상품 정보
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long price;

    private OrderItem(Long productId, String title, Long price) {
        this.productId = productId;
        this.title = title;
        this.price = price;
    }

    public static OrderItem create(Long productId, String title, Long price) {
        return new OrderItem(productId,title,price);
    }
}
