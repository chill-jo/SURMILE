package com.example.surveyapp.domain.survey.domain.model.entity;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyOutboxEnum;
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
@Table(name = "survey_outbox")
public class SurveyOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private Long aggregateId;

    @Lob
    private String payload;

    private boolean published;

    @Enumerated(EnumType.STRING)
    private SurveyOutboxEnum status;

    private int retryCount = 0;

    @CreatedDate
    private LocalDateTime publishedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private SurveyOutbox(String aggregateType, Long aggregateId, String payload, boolean published, SurveyOutboxEnum status, int retryCount) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.published = published;
        this.status = status;
        this.retryCount =retryCount;
        this.publishedAt = LocalDateTime.now();
    }

    public static SurveyOutbox of(String aggregateType, Long aggregateId, String payload) {
        return SurveyOutbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .published(false)
                .status(SurveyOutboxEnum.READY)
                .retryCount(0)
                .build();
    }

    public void markPublished() {
        this.published =true;
        this.status = SurveyOutboxEnum.PROCESSED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(int maxRetry) {
        this.retryCount++;
        if (retryCount >= maxRetry) {
           this.status = SurveyOutboxEnum.FAILED;
        }
        this.publishedAt = LocalDateTime.now();
    }
}
