package com.example.surveyapp.domain.ai.chat.application;

import com.example.surveyapp.domain.ai.chat.prompt.ChatPromptTemplate;
import com.example.surveyapp.domain.ai.chat.application.rag.DocumentSearcher;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 설문 앱 전용 챗봇 서비스.
 * - VectorStore에서 문서를 검색한 뒤,
 * - 프롬프트 템플릿에 컨텍스트(문서)와 질문을 주입하고,
 * - Spring AI ChatClient를 통해 모델 응답(컨텐츠 문자열)을 반환한다.
 */
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient chatClient;
    private final DocumentSearcher searcher;
    private final ChatPromptTemplate promptTemplate;

    /**
     * 사용자의 질문을 받아, 내부 지식을 문서로 보강하여 답변을 생성한다.
     *
     * @param question 사용자가 챗봇에 입력한 질문
     * @return 모델이 생성한 답변 텍스트
     */
    @Transactional
    public String chat(String question) {
        // 1) VectorStore에서 관련 문서 검색
        // - topK = 4: 상위 4개 문서
        // - similarityThreshold = 0.3: 문서 유사성 필터링 임계값
        List<Document> docs = searcher.search(question, 4, 0.3);

        // 2) 검색 결과 문서들의 본문을 하나의 컨텍스트 문자열로 합치기
        // --- 구분선을 넣어 모델이 출처 구분을 하기 쉽게 한다.
        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        // 3) 프롬프트 템플릿에 컨텍스트와 질문을 주입하여 Prompt 객체 생성
        Prompt prompt = promptTemplate.build(context, question);

        // 4) 모델 호출 → 결과 컨텐츠만 추출하여 반환
        return chatClient
                .prompt(prompt) // Prompt 설정
                .call()  // 호출
                .content(); // 최종 답변 텍스트
    }
}
