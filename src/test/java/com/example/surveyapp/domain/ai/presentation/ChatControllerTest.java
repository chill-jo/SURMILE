package com.example.surveyapp.domain.ai.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.domain.chat.application.ChatService;
import com.example.surveyapp.domain.chat.presentation.dto.ChatRequestDto;
import com.example.surveyapp.domain.chat.presentation.dto.ChatResponseDto;
import com.example.surveyapp.domain.chat.presentation.dto.IndexRequestDto;
import com.example.surveyapp.domain.chat.application.rag.DocumentIndexer;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Controller: Chat 컨트롤러 테스트")
@Import(TestMockBeans.class)
public class ChatControllerTest extends WebMvcTestBase {
    @Autowired
    private ChatService chatService;

    @Autowired
    private DocumentIndexer indexer;

    @Test
    @WithCustomMockUser(role = UserRoleEnum.SURVEYEE)
    @DisplayName("기능_테스트_챗봇_API를_호출하면_AI_응답을_생성한다")
    void 챗봇_API를_호출하면_AI_응답을_생성한다() throws Exception {
        // Given
        ChatRequestDto req = new ChatRequestDto("환불 규정 알려줘");
        ChatResponseDto res = new ChatResponseDto("마감 후 남은 예산은 환불됩니다.");
        when(chatService.chat(eq(req.getQuestion()))).thenReturn(res.getAnswer());

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/chat/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        );

        // Then
        verify(chatService, times(1)).chat(eq(req.getQuestion()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value(res.getAnswer()))
                .andDo(document("ai/ask-question",
                        requestFields(
                                fieldWithPath("question").type(JsonFieldType.STRING).description("챗봇 질문 내용")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.answer").type(JsonFieldType.STRING).description("답변 내용"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;
    }

    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    @DisplayName("기능_테스트_인덱싱_API를_관리자가_호출하면_문서가_인덱싱된다")
    public void 인덱싱_API를_관리자가_호출하면_성공() throws Exception {
        // Given
        IndexRequestDto dto = new IndexRequestDto("성공");

        // When
        ResultActions resultActions = mockMvc.perform(
                post("/api/chat/index")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        );

        // Then
        verify(indexer, times(1)).indexText(eq(1L), eq(dto.getDocument()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"))
                .andDo(document("ai/index-text",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        requestFields(
                                fieldWithPath("document").type(JsonFieldType.STRING).description("문서")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 Data"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;
    }
}
