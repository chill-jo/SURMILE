package com.example.surveyapp.domain.ai.chat.prompt;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChatPromptTemplate {
    private static final String chatPrompt = """
            당신은 설문조사 앱의 안내 챗봇입니다.
            아래 제공된 내부 문서만 근거로 답변하세요.
            반드시 출처 문맥에 근거해 답하고, 모르면 모른다고 말하세요.
            한국어로 간결하게 답변하세요.
            
            사용자가 "안녕", "너는 누구야?", "역할이 뭐야?" 등 챗봇에 관한 질문을 하면,
            당신은 본인의 역할을 간단히 소개해야 합니다.
            (예: "저는 설문조사 앱 "써마일"의 운영 정책과 사용 방법을 안내하는 챗봇입니다.")
            
            문서와 무관한 질문에는 반드시 다음처럼 답하세요:
            "해당 질문에는 답변할 수 없습니다."

            Context: {context}
            Question: {question}
            """;

    private final PromptTemplate template = new PromptTemplate(chatPrompt);

    public Prompt build(String context, String question) {
        return template.create(Map.of("context", context, "question", question));
    }
}
