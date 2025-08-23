package com.example.surveyapp.domain.ai.chat.application.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * VectorStore에서 유사도 기반으로 문서를 검색하는 컴포넌트.
 * - RAG 단계 중 Retrieve(검색) 역할 담당.
 */
@Component
@RequiredArgsConstructor
public class DocumentSearcher {
    private final VectorStore vectorStore; // Vectorstore 구현체

    /**
     * 질의(query)를 임베딩하여 벡터 검색을 수행한다.
     *
     * @param query 사용자 질문(텍스트)
     * @param topK 상위 몇 개의 문서를 가져올지
     * @param similarityThreshold 유사도 필터링(범위 0~1)
     * @return 검색된 문서 리스트(관련도 순)
     */
    public List<Document> search(String query, int topK, double similarityThreshold) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(similarityThreshold)
                        .build()
        );
    }
}
