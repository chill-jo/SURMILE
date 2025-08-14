package com.example.surveyapp.domain.product.domain.model;

import com.example.surveyapp.domain.product.exception.ProductErrorCode;
import com.example.surveyapp.domain.product.exception.ProductException;
import com.example.surveyapp.global.config.entity.BaseEntity;
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

    @Embedded
    private ProductPoints price;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder(access = AccessLevel.PRIVATE)
    private Product(String title, ProductPoints price, String content, Status status) {
        this.title = title;
        this.price = price;
        this.content = content;
        this.status = status;
    }

    public static Product of(String title,
                             ProductPoints price,
                             String content,
                             Status status) {
        if (price.isZero()){
            throw new ProductException(ProductErrorCode.INVALID_PRICE_PRODUCT);
        }
        return Product.builder()
                .title(title)
                .price(price)
                .content(content)
                .status(status)
                .build();
    }

    /**
     * 상태 전환 규칙
     * 동일 상태면 바로 return
     * STOPPED_SALE -> ON_SALE 상태 변경 시 0원 금지 예외처리
     * @param newStatus
     */
    public void changeStatus(Status newStatus) {

        productPriceZeroOrThrow();
        if (status == null || this.status == newStatus) return;

        if (this.status == Status.STOPPED_SALE && newStatus == Status.ON_SALE){
            productPriceZeroOrThrow();
        }
        this.status = newStatus;
    }

    /**
     * 판매중단 제품 상태 예외
     */
    public void getStatusStoppedSaleOrThrow(){
        if (status == Status.STOPPED_SALE) {
            throw new ProductException(ProductErrorCode.NOT_SALE_PRODUCT);
        }
    }

    public void delete() {

        if (this.status == Status.ON_SALE) {
            throw new ProductException(ProductErrorCode.NOT_DELETE_ON_SALE_PRODUCT);
        }

        this.isDeleted = true;
    }

    public void update(String title, ProductPoints price, String content, Status status) {
        if (title != null) this.title = title;
        if (price != null)  this.price = price;
        if (content != null)  this.content = content;
        if (status != null)  this.status = status;
    }

    /**
     * 금액이 0원이면 예외 처리
     */
    public void productPriceZeroOrThrow(){
        if (price == null || price.isZero()) {
            throw new ProductException(ProductErrorCode.NOT_PRODUCT_PRICE_ZERO);
        }
    }

}