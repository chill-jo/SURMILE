package com.example.surveyapp.domain.moderation.domain.repository;

import com.example.surveyapp.domain.moderation.domain.model.AiModeration;

public interface AiModerationRepository {
    AiModeration save(AiModeration aiModeration);
}
