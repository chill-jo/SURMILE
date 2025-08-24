package com.example.surveyapp.domain.moderation.infrastructure;

import com.example.surveyapp.domain.moderation.domain.model.AiModeration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiModerationJpaRepository extends JpaRepository<AiModeration, Long> {
}
