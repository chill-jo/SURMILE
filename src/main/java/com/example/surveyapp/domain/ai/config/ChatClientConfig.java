package com.example.surveyapp.domain.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI ChatClient 설정 클래스.
 * - ChatClient.Builder를 기반으로 ChatClient Bean을 생성한다.
 * - ChatClient는 모델 호출을 캡슐화한 핵심 API.
 */
@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }
}
