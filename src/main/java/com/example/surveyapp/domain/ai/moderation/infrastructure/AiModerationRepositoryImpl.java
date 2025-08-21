package com.example.surveyapp.domain.ai.moderation.infrastructure;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModeration;
import com.example.surveyapp.domain.ai.moderation.domain.repository.AiModerationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AiModerationRepositoryImpl implements AiModerationRepository {
    private final AiModerationJpaRepository aiModerationJpaRepository;

    @Override
    public AiModeration save(AiModeration aiModeration){return aiModerationJpaRepository.save(aiModeration);}
}
