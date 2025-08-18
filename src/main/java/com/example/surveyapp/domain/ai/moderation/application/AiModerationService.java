package com.example.surveyapp.domain.ai.moderation.application;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModeration;
import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.repository.AiModerationRepository;
import com.example.surveyapp.domain.ai.moderation.prompt.AiModerationPromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiModerationService {
    private final ChatClient chatClient;
    private final AiModerationRepository moderationRepository;

    @Transactional
    public AiModerationResultStatusEnum moderate(AiModerationTargetType targetType, String content) {
        // 프롬프트 구성
        PromptTemplate prompt = new PromptTemplate(AiModerationPromptTemplate.promptTemplate);
        prompt.add("targetType", targetType);
        prompt.add("content", content);

        // AI 호출
        String result = chatClient.prompt()
                .user(user -> user.text(prompt.render()))
                .call()
                .content()
                .trim()
                .toUpperCase();

        AiModerationResultStatusEnum status = result.startsWith("DENIED") ? AiModerationResultStatusEnum.DENIED : AiModerationResultStatusEnum.APPROVED;

        // 결과가 DENIED면 저장
        if (status == AiModerationResultStatusEnum.DENIED) {
            AiModeration saved = moderationRepository.save(AiModeration.of(targetType, content));
        }
        return status;
    }
}
