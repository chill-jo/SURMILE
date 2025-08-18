package com.example.surveyapp.domain.ai.moderation.domain.model;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
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
public class Moderation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetType; // user_nickname, survey_title, survey_description, survey_question, survey_options, survey_text_answer

    @Column(nullable = false)
    private String content;

    private boolean isDeleted = false;

    @Builder(access = AccessLevel.PRIVATE)
    private Moderation(String targetType, String content) {
        this.targetType = targetType;
        this.content = content;
    }

    public static Moderation of(String targetType, String content) {
        return Moderation.builder()
                .targetType(targetType)
                .content(content)
                .build();
    }

    public void softDelete(){
        this.isDeleted = true;
    }
}
