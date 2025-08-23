package com.example.surveyapp.domain.user.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users_outbox")
public class UserOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private Long aggregateId;

    @Lob
    private String payload;

    private boolean published;

    private int retryCount = 0;

    private UserOutboxEnum status;

    @CreatedDate
    private LocalDateTime publishedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private UserOutbox(String aggregateType, Long aggregateId, String payload, boolean published, UserOutboxEnum status, int retryCount) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.published = published;
        this.status = status;
        this.retryCount = retryCount;
        this.publishedAt = LocalDateTime.now();
    }

    public static UserOutbox of(String aggregateType, Long aggregateId, String payload) {
        return   UserOutbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .published(false)
                .status(UserOutboxEnum.READY)
                .retryCount(0)
                .build();
    }

    public void markPublished(){
        this.published = true;
        this.status = UserOutboxEnum.PROCESSED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(int maxRetry) {
        this.retryCount++;
        if(retryCount >= maxRetry){
            this.status = UserOutboxEnum.FAILED;
        }
        log.info("지금 실패해서 다시 돌아유");
        this.publishedAt = LocalDateTime.now();
    }
}

