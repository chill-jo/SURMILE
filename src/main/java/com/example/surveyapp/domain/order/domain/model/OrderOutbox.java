package com.example.surveyapp.domain.order.domain.model;

import com.example.surveyapp.domain.order.domain.model.vo.OrderOutBoxEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_outbox")
public class OrderOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private Long aggregateId;

    @Lob
    private String payload;

    private boolean published;

    @Enumerated(EnumType.STRING)
    private OrderOutBoxEnum status;

    private int retryCount = 0;

    @CreatedDate
    private LocalDateTime publishedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderOutbox(String aggregateType, Long aggregateId, String payload, boolean published,OrderOutBoxEnum status,int retryCount) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.published = published;
        this.status = status;
        this.retryCount = retryCount;
    }

    public static OrderOutbox of(String aggregateType, Long aggregateId, String payload) {
        return OrderOutbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .published(false)
                .status(OrderOutBoxEnum.READY)
                .retryCount(0)
                .build();
    }

    public void markPublished() {
            this.published = true;
            this.status = OrderOutBoxEnum.PROCESSED;
            this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(int maxRetry) {
        this.retryCount++;
        if (retryCount >= maxRetry) {
            this.status = OrderOutBoxEnum.FAILED;
        }
            this.publishedAt = LocalDateTime.now();

    }
}

