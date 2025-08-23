package com.example.surveyapp.domain.ai.chat.application.rag;

import com.example.surveyapp.domain.ai.exception.AiErrorCode;
import com.example.surveyapp.global.reader.UserReader;
import com.example.surveyapp.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * VectorStore에 텍스트 문서를 임베딩(벡터화) 후 저장(인덱싱)하는 컴포넌트.
 * - 관리자가 운영 정책/가이드라인 등의 문서를 추가할 때 사용.
 */
@Component
@RequiredArgsConstructor
public class DocumentIndexer {
    private final VectorStore vectorStore; // 벡터 임베딩/검색을 제공하는 VectorStore 구현체
    private final UserReader userReader;

    /**
     * 단일 텍스트를 VectorStore에 인덱싱한다.
     * @param userId   인덱싱 요청자 ID (관리자만 허용)
     * @param content  저장할 본문 내용
     */
    public void indexText(Long userId, String content) {
        validateIndexerAccess(userId);

        Document doc = new Document(content); // 텍스트 → Document 래핑
        vectorStore.add(List.of(doc)); // VectorStore에 추가 (내부에서 임베딩 생성 → 저장)
    }

    /**
     * 인덱싱 기능 접근 권한을 검증한다.
     * - userId가 null이거나, 관리자가 아니면 예외 발생.
     */
    public void validateIndexerAccess(Long userId){
        if (userId == null || !userReader.validateUserRoleToAdmin(userId)) {
            throw new CustomException(AiErrorCode.NOT_MATCH_ADMIN);
        }
    }
}
