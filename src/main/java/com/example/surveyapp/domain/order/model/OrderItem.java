package com.example.surveyapp.domain.order.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    /**
     * 주문 당시 상품 정보
     */
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long price;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderItem(Long productId, String title, Long price) {
        this.productId = productId;
        this.title = title;
        this.price = price;
    }

    public static OrderItem create(Long productId, String title, Long price) {
        return OrderItem.builder()
                .productId(productId)
                .title(title)
                .price(price)
                .build();
    }
}
