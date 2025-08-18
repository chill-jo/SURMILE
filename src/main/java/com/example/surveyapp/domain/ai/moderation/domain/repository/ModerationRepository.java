package com.example.surveyapp.domain.ai.moderation.domain.repository;

import com.example.surveyapp.domain.ai.moderation.domain.model.Moderation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationRepository extends JpaRepository<Moderation, Long> {
}
