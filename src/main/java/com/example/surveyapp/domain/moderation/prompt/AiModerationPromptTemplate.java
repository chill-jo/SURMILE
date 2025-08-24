package com.example.surveyapp.domain.moderation.prompt;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AiModerationPromptTemplate {
    public static final String moderationPrompt = """
        You are a content moderation assistant.
        Determine if the following {targetType} is inappropriate.
        
        Inappropriate examples include:
        - profanity(excluding "바보", "멍청이")
        - hate speech
        - sexual content
        - violence
        - offensive or disturbing expressions
        
        Respond with exactly one word, either 'APPROVED' or 'DENIED', no other text.
        
        Content: {content}
        """;

    // Spring AI의 PromptTemplate 객체로 컴파일
    private final PromptTemplate template = new PromptTemplate(moderationPrompt);

    // 주어진 targetType와 content을 바인딩하여 Prompt 객체 생성
    public Prompt build(String targetType, String content) {
        return template.create(Map.of("targetType", targetType, "content", content));
    }
}
