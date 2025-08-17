package com.example.surveyapp.domain.ai.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.domain.ai.chat.application.ChatService;
import com.example.surveyapp.domain.ai.chat.presentation.dto.ChatRequestDto;
import com.example.surveyapp.domain.ai.chat.presentation.dto.ChatResponseDto;
import com.example.surveyapp.domain.ai.chat.presentation.dto.IndexRequestDto;
import com.example.surveyapp.domain.ai.chat.rag.DocumentIndexer;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.config.SecurityConfig;
import com.example.surveyapp.global.filter.JwtFilter;
import com.example.surveyapp.global.security.handler.CustomAccessDeniedHandler;
import com.example.surveyapp.global.security.handler.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@Import({TestMockBeans.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = true)
public class ChatControllerTest extends WebMvcTestBase {
    @Autowired
    private ChatService chatService;

    @MockitoBean
    private DocumentIndexer indexer;

    @MockitoBean
    JwtFilter jwtFilter;

    @MockitoBean
    CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    CustomAccessDeniedHandler accessDeniedHandler;

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

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value(res.getAnswer()));
    }

    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    @DisplayName("기능_테스트_인덱싱_API를_관리자가_호출하면_문서가_인덱싱된다")
    public void 인덱싱_API를_관리자가_호출하면_성공() throws Exception {
        // Given
        IndexRequestDto dto = new IndexRequestDto("이 문서를 인덱싱합니다.");

        // When
        ResultActions resultActions = mockMvc.perform(
                post("/api/chat/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        );

        // Then
        verify(indexer, times(1)).indexText(eq(dto.getDocument()));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문서가 인덱싱되었습니다."))
        ;
    }

    @Test
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    @DisplayName("예외_테스트_인덱싱_API를_일반_회원이_호출하면_예외처리_된다")
    public void 인덱싱_API를_일반_회원이_호출하면_예외처리() throws Exception {
        // Given
        IndexRequestDto dto = new IndexRequestDto("이 문서를 인덱싱합니다.");

        // When
        ResultActions resultActions = mockMvc.perform(
                post("/api/chat/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        );

        // Then
        verify(indexer, never()).indexText(anyString());

        resultActions.andDo(print())
                .andExpect(status().isForbidden());
    }
}
