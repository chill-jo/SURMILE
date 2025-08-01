package com.example.surveyapp.domain.ai.moderation.nickname.prompt;

public class NicknameModerationPromptTemplate {
    public static final String promptTemplate = """
            You are a content moderation assistant.
            Determine if the following nickname is inappropriate.

            Inappropriate examples include:
            - profanity
            - hate speech
            - sexual content
            - violence
            - offensive or disturbing expressions

            Respond with one word only: APPROVED or DENIED

            Nickname: {nickname}
        """;
}
