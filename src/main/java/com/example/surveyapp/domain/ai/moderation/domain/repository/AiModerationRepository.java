package com.example.surveyapp.domain.ai.moderation.domain.repository;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModeration;

public interface AiModerationRepository {
    AiModeration save(AiModeration aiModeration);
}
