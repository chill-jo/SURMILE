package com.example.surveyapp.domain.ai.moderation.domain.model;

import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "moderation_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModeration extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AiModerationTargetType targetType;

    @Column(nullable = false)
    private String content;

    private boolean isDeleted = false;

    @Builder(access = AccessLevel.PRIVATE)
    private AiModeration(AiModerationTargetType targetType, String content) {
        this.targetType = targetType;
        this.content = content;
    }

    public static AiModeration of(AiModerationTargetType targetType, String content) {
        return AiModeration.builder()
                .targetType(targetType)
                .content(content)
                .build();
    }

    public void softDelete(){
        this.isDeleted = true;
    }
}
