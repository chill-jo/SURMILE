package com.example.surveyapp.domain.ai.chat.prompt;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    private final PromptTemplate template = new PromptTemplate(chatPrompt);

    public Prompt build(String context, String question) {
        return template.create(Map.of("context", context, "question", question));
    }
}
