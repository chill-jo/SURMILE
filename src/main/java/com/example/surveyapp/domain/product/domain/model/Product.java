package com.example.surveyapp.domain.product.domain.model;

import com.example.surveyapp.global.config.entity.BaseEntity;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String title;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder(access = AccessLevel.PRIVATE)
    private Product(String title, Long price, String content, Status status) {
        this.title = title;
        this.price = price;
        this.content = content;
        this.status = status;
    }

    public static Product create(String title, Long price, String content, Status status) {
        return Product.builder()
                .title(title)
                .price(price)
                .content(content)
                .status(status)
                .build();
    }

    /**
     *
     * @param newStatus
     */
    public void changeStatus(Status newStatus) {
        if (this.status == newStatus) return;

         if (this.status == Status.STOPPED_SALE && newStatus == Status.ON_SALE){
            if (price == 0) {
                throw new CustomException(ErrorCode.NOT_PRODUCT_PRICE_ZERO);
            }
        }
        this.status = newStatus;
    }

    public void delete() {

        if (this.status == Status.ON_SALE) {
            throw new CustomException(ErrorCode.NOT_DELETE_ON_SALE_PRODUCT);
        }

        this.isDeleted = true;
    }

    public void update(String title, Long price, String content, Status status) {
        if (title != null) this.title = title;
        if (price != null)  this.price = price;
        if (content != null)  this.content = content;
        if (status != null)  this.status = status;
    }

}