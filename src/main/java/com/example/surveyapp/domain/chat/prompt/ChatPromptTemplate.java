package com.example.surveyapp.domain.chat.prompt;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 챗봇 프롬프트 템플릿 정의 클래스.
 * - RAG로 검색된 컨텍스트와 사용자 질문을 합쳐서 Prompt 객체를 만든다.
 * - 모델이 불필요하게 외부 지식을 생성하지 않고, 앱 운영 정책에 맞는 답변만 하도록 지침.
 * - 영어 데이터 기반 학습량이 많은 OpenAI 모델 특성을 고려하여 지침을 영어로 작성.
 * - 최종답변은 한국어로 생성하게 지침.
 */
@Component
public class ChatPromptTemplate {
    private static final String chatPrompt = """
            You are a guide chatbot for a survey application.
            Answer only based on the internal documents provided below.
            You must always ground your answers in the given context, and if you don’t know, say that you don’t know.
            Always respond concisely in Korean.
            
            If the user asks greetings or about the chatbot itself
            (for example: "안녕", "너는 누구야?", "역할이 뭐야?"),
            you should briefly introduce your role.
            (e.g., "저는 설문조사 앱 '써마일'의 운영 정책과 사용 방법을 안내하는 챗봇입니다.")
            
            For questions unrelated to the documents, always answer exactly as follows:
            "해당 질문에는 답변할 수 없습니다."
            
            Context: {context}
            Question: {question}
            """;

    // Spring AI의 PromptTemplate 객체로 컴파일
    private final PromptTemplate template = new PromptTemplate(chatPrompt);

    // 주어진 context와 question을 바인딩하여 Prompt 객체 생성
    public Prompt build(String context, String question) {
        return template.create(Map.of("context", context, "question", question));
    }
}
