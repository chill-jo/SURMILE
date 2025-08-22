package com.example.surveyapp.domain.point.domain.model.entity;

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
@Table(name = "point_outbox")
public class PointOutbox {

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
    private PointOutboxEnum status;

    @CreatedDate
    private LocalDateTime publishedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private PointOutbox(String aggregateType, Long aggregateId, String payload, boolean published, PointOutboxEnum status ,int retryCount) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.published = published;
        this.status =status;
        this.retryCount = retryCount;
        this.publishedAt = LocalDateTime.now();
    }

    public static PointOutbox of(String aggregateType, Long aggregateId, String payload) {
        return PointOutbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .published(false)
                .status(PointOutboxEnum.READY)
                .retryCount(0)
                .build();
    }

    public void markPublished() {
        this.published = true;
        this.status = PointOutboxEnum.PROCESSED.PROCESSED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(int maxRetry) {
        this.retryCount++;
        if (retryCount >= maxRetry) {
            this.status = PointOutboxEnum.FAILED.FAILED;
        }
        this.publishedAt = LocalDateTime.now();

    }
}
