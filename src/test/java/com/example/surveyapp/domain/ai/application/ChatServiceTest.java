package com.example.surveyapp.domain.ai.application;

import com.example.surveyapp.domain.ai.chat.prompt.ChatPromptTemplate;
import com.example.surveyapp.domain.ai.chat.application.rag.DocumentIndexer;
import com.example.surveyapp.domain.ai.chat.application.rag.DocumentSearcher;
import com.example.surveyapp.domain.ai.chat.application.ChatService;
import com.example.surveyapp.global.reader.UserReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service: Chat 서비스 테스트")
public class ChatServiceTest {
    @InjectMocks
    private ChatService chatService;

    @InjectMocks
    private DocumentIndexer indexer;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatPromptTemplate template;

    @Mock
    private DocumentSearcher searcher;

    @Mock
    private UserReader oauthReader;

    @Test
    @DisplayName("기능_테스트_사용자가_질문하면_LLM_응답을_생성한다")
    void 사용자가_질문하면_LLM_응답을_생성한다(){
        // Given
        String question = "환불 규정 알려줘";
        List<Document> docs = List.of(
                new Document("마감 후 남은 예산은 환불됩니다."),
                new Document("포인트는 즉시 지급됩니다.")
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
        assertThat(answer).contains("환불");
        verify(searcher).search(eq(question), anyInt(), anyDouble());
        verify(template).build(eq(expectedContext), eq(question));
    }

    @Test
    @DisplayName("기능_테스트_관리자가_문서를_추가하면_인덱싱된다")
    void 관리자가_문서를_추가하면_인덱싱된다() {
        // Given
        String doc = "이 문서를 인덱싱합니다.";
        when(oauthReader.validateUserRoleToAdmin(1L)).thenReturn(true);

        // When
        indexer.indexText(1L, doc);

        // Then
        verify(oauthReader).validateUserRoleToAdmin(1L);
        verify(vectorStore).add(argThat(list ->
                list != null && list.size() == 1 && doc.equals(list.get(0).getText())
        ));
    }
}
