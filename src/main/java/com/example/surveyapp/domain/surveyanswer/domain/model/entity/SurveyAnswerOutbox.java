package com.example.surveyapp.domain.surveyanswer.domain.model.entity;

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
@Table(name = "surveyanswer_outbox")
public class SurveyAnswerOutbox {

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
    private AnserEnum status;

    @CreatedDate
    private LocalDateTime publishedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private SurveyAnswerOutbox(String aggregateType, Long aggregateId, String payload, boolean published, AnserEnum status, int retryCount) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.published = published;
        this.status = status;
        this.retryCount = retryCount;
        this.publishedAt = LocalDateTime.now();
    }

    public static SurveyAnswerOutbox of(String aggregateType, Long aggregateId, String payload) {
        return SurveyAnswerOutbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .published(false)
                .status(AnserEnum.READY)
                .retryCount(0)
                .build();
    }

    public void markPublished() {
        this.published = true;
        this.status = AnserEnum.PROCESSED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(int maxRetry) {
        this.retryCount++;
        if (retryCount >= maxRetry){
            this.status = AnserEnum.FAILED;
        }
        this.publishedAt = LocalDateTime.now();
    }
}
