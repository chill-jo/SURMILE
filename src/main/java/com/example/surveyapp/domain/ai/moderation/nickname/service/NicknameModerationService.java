package com.example.surveyapp.domain.ai.moderation.nickname.service;

import com.example.surveyapp.domain.ai.moderation.config.ModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.nickname.controller.dto.NicknameModerationRequestDto;
import com.example.surveyapp.domain.ai.moderation.nickname.controller.dto.NicknameModerationResponseDto;
import com.example.surveyapp.domain.ai.moderation.nickname.prompt.NicknameModerationPromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NicknameModerationService {
    public final ChatClient chatClient;

    public NicknameModerationResponseDto moderate(NicknameModerationRequestDto request) {
        PromptTemplate prompt = new PromptTemplate(NicknameModerationPromptTemplate.promptTemplate);
        prompt.add("nickname", request.getNickname());

        String result = chatClient.prompt()
                .user(user -> user.text(prompt.render()))
                .call()
                .content()
                .trim()
                .toUpperCase();

        ModerationResultStatusEnum status = result.startsWith("DENIED") ? ModerationResultStatusEnum.DENIED : ModerationResultStatusEnum.APPROVED;

        String reason = status == ModerationResultStatusEnum.DENIED ? "부적절한 닉네임입니다." : null;

        return new NicknameModerationResponseDto(status, reason);
    }
}