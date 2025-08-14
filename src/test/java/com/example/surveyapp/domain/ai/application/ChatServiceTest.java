package com.example.surveyapp.domain.ai.application;

import com.example.surveyapp.domain.ai.chat.prompt.ChatPromptTemplate;
import com.example.surveyapp.domain.ai.chat.rag.DocumentSearcher;
import com.example.surveyapp.domain.ai.chat.application.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service: Chat 서비스 테스트")
public class ChatServiceTest {
    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatPromptTemplate template;

    @Mock
    private DocumentSearcher searcher;

    @Test
    @DisplayName("기능_테스트_사용자가_질문하면_LLM_응답을_생성한다")
    void 사용자가_질문하면_LLM_응답을_생성한다(){
        // Given
        String question = "출제자는 어떤 기능을 이용할 수 있어?";
        List<Document> docs = List.of(
                new Document("출제자는 회원 가입 시 출제자로 선택해야 설문조사 등록 등 출제자의 기능을 이용할 수 있습니다."),
                new Document("출제자는 이메일과 비밀번호로 로그인할 수 있으며, 참여자 기능(상점, 설문 참여)을 이용할 수 없습니다.")
        );
        when(searcher.search(eq(question), anyInt(), anyDouble())).thenReturn(docs);

        String expectedContext = docs.stream()
                .map(Document::getText)
                .collect(java.util.stream.Collectors.joining("\n---\n"));

        Prompt prompt = mock(Prompt.class);
        when(template.build(eq(expectedContext), eq(question))).thenReturn(prompt);

        when(chatClient.prompt(eq(prompt)).call().content())
                .thenReturn("마감 후 남은 예산은 환불됩니다.");

        // When
        String answer = chatService.chat(question);

        // Then
        assertThat(answer).contains("출제자");
        verify(template).build(eq(expectedContext), eq(question));
    }

}
