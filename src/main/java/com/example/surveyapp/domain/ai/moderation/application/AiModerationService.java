package com.example.surveyapp.domain.ai.moderation.application;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModeration;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.repository.AiModerationRepository;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.ai.moderation.prompt.AiModerationPromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiModerationService {
    private final ChatClient chatClient;
    private final AiModerationRepository aiModerationRepository;
    private final AiModerationPromptTemplate moderationPromptTemplate;

    @Transactional
    public AiModerationResult moderate(Long userId, String email, AiModerationTargetType targetType, String content) {
        // 프롬프트 구성
        Prompt prompt = moderationPromptTemplate.build(targetType.name(), content);

        // AI 호출
        String result = chatClient
                .prompt(prompt)
                .call()
                .content()
                .trim()
                .toUpperCase();

        AiModerationResultStatusEnum status = result.startsWith("DENIED") ? AiModerationResultStatusEnum.DENIED : AiModerationResultStatusEnum.APPROVED;

        // 결과가 DENIED면 저장
        if (status == AiModerationResultStatusEnum.DENIED) {
            AiModeration moderation = AiModeration.of(userId, email, targetType, content);
            aiModerationRepository.save(moderation);

            log.warn("Inappropriate content detected. Target={}, Content={}", targetType, content);

            return AiModerationResult.of(moderation.getId(), status);
        }
        log.debug("[AI-MODERATION-APPROVED] targetType={}", targetType);
        return AiModerationResult.of(null, status);
    }
}
