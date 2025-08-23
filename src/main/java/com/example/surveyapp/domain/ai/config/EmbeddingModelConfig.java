package com.example.surveyapp.domain.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 임베딩 모델 및 벡터스토어 설정.
 * - Spring AI의 EmbeddingModel을 주입받아 VectorStore를 구성한다.
 */
@Configuration
public class EmbeddingModelConfig {
    @Bean
    // SimpleVectorStore: In-memory VectorStore(영속 x). 실제 운영 시에는 외부 스토리지 연동 고려.
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
