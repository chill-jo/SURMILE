package com.example.surveyapp.domain.ai.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.domain.ai.chat.application.ChatService;
import com.example.surveyapp.domain.ai.chat.rag.DocumentIndexer;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Controller: Chat 컨트롤러 테스트")
@Import(TestMockBeans.class)
public class ChatControllerTest extends WebMvcTestBase {
    @Autowired
    private ChatService chatService;

    @MockitoBean
    private DocumentIndexer indexer;

    @Test
    @WithCustomMockUser(role = UserRoleEnum.ADMIN)
    @DisplayName("기능_테스트_챗봇_API를_호출하면_AI_응답을_생성한다")
    void 챗봇_API를_호출하면_AI_응답을_생성한다() throws Exception {
        // Given
        String question = "환불 규정 알려줘";
        String answer = "마감 후 남은 예산은 환불됩니다.";
        when(chatService.chat(eq(question))).thenReturn(answer);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/chat/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"환불 규정 알려줘"}
                                """)
        );

        // Then
        verify(chatService, times(1)).chat(eq(question));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value(answer));
    }
}
