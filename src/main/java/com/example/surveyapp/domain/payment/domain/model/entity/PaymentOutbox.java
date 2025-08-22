package com.example.surveyapp.domain.payment.domain.model.entity;

import com.example.surveyapp.domain.payment.domain.model.enums.PaymentOutboxEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Table(name = "payment_outbox")
public class PaymentOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private Long aggregateId;

    @Lob
    private String payload;

    private boolean published;

    private int retryCount = 0;

    @Enumerated(EnumType.STRING)
    private PaymentOutboxEnum status;

    @CreatedDate
    private LocalDateTime publishedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private PaymentOutbox(String aggregateType, Long aggregateId, String payload, boolean published, PaymentOutboxEnum status ,int retryCount) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.published = published;
        this.status =status;
        this.retryCount = retryCount;
        this.publishedAt = LocalDateTime.now();
    }

    public static PaymentOutbox of(String aggregateType, Long aggregateId, String payload) {
        return PaymentOutbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .published(false)
                .status(PaymentOutboxEnum.READY)
                .retryCount(0)
                .build();
    }

    public void markPublished() {
        this.published = true;
        this.status = PaymentOutboxEnum.PROCESSED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(int maxRetry) {
        this.retryCount++;
        if (retryCount >= maxRetry) {
            this.status = PaymentOutboxEnum.FAILED;
        }
        this.publishedAt = LocalDateTime.now();

    }

}
