package com.example.surveyapp.domain.ai.moderation.prompt;

public class AiModerationPromptTemplate {
    public static final String promptTemplate = """
        You are a content moderation assistant.
        Determine if the following {targetType} is inappropriate.
        
        Inappropriate examples include:
        - profanity(excluding "바보", "멍청이")
        - hate speech
        - sexual content
        - violence
        - offensive or disturbing expressions
        
        Respond with one word only: APPROVED or DENIED
        
        Content: {content}
        """;
}
