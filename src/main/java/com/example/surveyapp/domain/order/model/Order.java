package com.example.surveyapp.domain.order.model;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 36)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

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
    private Order(User user, Product product, String title, Long price, String orderNumber) {
        this.user = user;
        this.product = product;
        this.title = title;
        this.price = price;
        this.orderNumber = orderNumber;
    }

    public static Order create(User user, Product product) {

        String orderNumber = orderNumberGenerator();
        return Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .product(product)
                .title(product.getTitle())
                .price(product.getPrice())
                .build();
    }


    //UUID 날짜 + 랜덤 숫자 8개를 만들기 위한 generator
    private static String orderNumberGenerator() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randoNum = (int) ((Math.random() * 90000000) + 10000000); //8자리 랜덤 숫자 만들기
        return date + randoNum;
    }
}
