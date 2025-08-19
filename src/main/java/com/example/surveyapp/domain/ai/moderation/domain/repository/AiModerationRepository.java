package com.example.surveyapp.domain.ai.moderation.domain.repository;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModeration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiModerationRepository extends JpaRepository<AiModeration, Long> {
}
