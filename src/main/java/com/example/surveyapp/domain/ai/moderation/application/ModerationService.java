package com.example.surveyapp.domain.ai.moderation.application;

import com.example.surveyapp.domain.ai.moderation.domain.model.Moderation;
import com.example.surveyapp.domain.ai.moderation.domain.model.ModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.domain.model.ModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.repository.ModerationRepository;
import com.example.surveyapp.domain.ai.moderation.prompt.ModerationPromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModerationService {
    private final ChatClient chatClient;
    private final ModerationRepository moderationRepository;

    @Transactional
    public ModerationResultStatusEnum moderate(ModerationTargetType targetType, String content) {
        // 프롬프트 구성
        PromptTemplate prompt = new PromptTemplate(ModerationPromptTemplate.promptTemplate);
        prompt.add("targetType", targetType);
        prompt.add("content", content);

        // AI 호출
        String result = chatClient.prompt()
                .user(user -> user.text(prompt.render()))
                .call()
                .content()
                .trim()
                .toUpperCase();

        ModerationResultStatusEnum status = result.startsWith("DENIED") ? ModerationResultStatusEnum.DENIED : ModerationResultStatusEnum.APPROVED;

        // 결과가 DENIED면 저장
        if (status == ModerationResultStatusEnum.DENIED) {
            Moderation saved = moderationRepository.save(Moderation.of(targetType, content));
        }
        return status;
    }
}
