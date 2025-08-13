package com.example.surveyapp.domain.ai.moderation.application;

import com.example.surveyapp.domain.ai.moderation.config.ModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.prompt.ModerationPromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {
    private final ChatClient chatClient;

    public ModerationResultStatusEnum moderate(String targetType, String content) {
        PromptTemplate prompt = new PromptTemplate(ModerationPromptTemplate.promptTemplate);
        prompt.add("targetType", targetType);
        prompt.add("content", content);

        String result = chatClient.prompt()
                .user(user -> user.text(prompt.render()))
                .call()
                .content()
                .trim()
                .toUpperCase();

        return result.startsWith("DENIED") ? ModerationResultStatusEnum.DENIED : ModerationResultStatusEnum.APPROVED;
    }
}
