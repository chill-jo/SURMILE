package com.example.surveyapp.domain.survey.presentation;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.domain.survey.application.QuestionService;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.*;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("controller : QuestionController 테스트")
@Import(TestMockBeans.class)
public class QuestionControllerTest extends WebMvcTestBase {

    @Autowired
    private QuestionService questionService;

    @Test
    @DisplayName("기능_질문을 생성한다.")
    @WithCustomMockUser(id = 2L, role = UserRoleEnum.SURVEYOR)
    void 질문_생성() throws Exception{

        Long userId = 2L;
        Long surveyId = 1L;

        QuestionCreateRequestDto requestDto = new QuestionCreateRequestDto(
                1L,
                "테스트질문내용",
                QuestionType.SINGLE_CHOICE
        );

        QuestionResponseDto responseDto = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용",
                QuestionType.SINGLE_CHOICE
        );

        when(questionService.createQuestion(eq(userId), eq(surveyId), any(QuestionCreateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/survey/{surveyId}", surveyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(questionService, times(1))
                .createQuestion(eq(userId), eq(surveyId), any(QuestionCreateRequestDto.class));

        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.number").value(requestDto.getNumber()))
                .andExpect(jsonPath("$.data.content").value(requestDto.getContent()))
                .andDo(document("question/create-question",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("surveyId").description("설문 ID")
                        ),
                        requestFields(
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("질문 번호"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("질문 유형")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 질문 위치")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("질문 번호"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("질문 유형")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_질문목록을_조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    void 참여자가_질문목록을_조회한다() throws Exception{
        int page = 0;
        int size = 2;
        Long userId = 1L;
        Long surveyId = 1L;

        QuestionResponseDto responseDto1 = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용1",
                QuestionType.SINGLE_CHOICE
        );
        QuestionResponseDto responseDto2 = new QuestionResponseDto(
                1L,
                2L,
                "테스트질문내용2",
                QuestionType.SUBJECTIVE
        );
        List<QuestionResponseDto> dtoList = List.of(responseDto1, responseDto2);
        Page<QuestionResponseDto> pageDto = new PageImpl<>(dtoList, PageRequest.of(page, size), dtoList.size());
        PageQuestionResponseDto<QuestionResponseDto> pageQuestionResponseDto =
                new PageQuestionResponseDto<>(pageDto);

        when(questionService.getQuestions(page, size, userId, surveyId)).thenReturn(pageQuestionResponseDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/{surveyId}/question", surveyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        verify(questionService, times(1))
                .getQuestions(page, size, userId, surveyId);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].content").value("테스트질문내용1"))
                .andExpect(jsonPath("$.data.content[1].content").value("테스트질문내용2"))
                .andDo(document("question/get-questions",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("surveyId").description("설문 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("data.content[].number").type(JsonFieldType.NUMBER).description("질문 번호"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("data.content[].type").type(JsonFieldType.STRING).description("질문 유형"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 결과 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("페이지 수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 size"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("페이지 number")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_질문_단건조회한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 질문_단건조회한다() throws Exception{
        Long surveyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;

        QuestionResponseDto responseDto = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용",
                QuestionType.SINGLE_CHOICE
        );

        when(questionService.getQuestion(eq(userId), eq(surveyId), eq(questionId))).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(get("/api/survey/{surveyId}/question/{questionId}", surveyId, questionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}"));

        verify(questionService, times(1))
                .getQuestion(eq(userId), eq(surveyId), eq(questionId));

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("테스트질문내용"))
                .andExpect(jsonPath("$.data.number").value(responseDto.getNumber()))
                .andExpect(jsonPath("$.data.type").value(responseDto.getType().name()))
                .andDo(document("question/get-question",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("surveyId").description("설문 ID"),
                                parameterWithName("questionId").description("질문 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("질문 번호"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("질문 유형")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_질문_상세정보를_수정한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 질문_상세정보를_수정한다() throws Exception{
        Long surveyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;
        QuestionUpdateRequestDto requestDto = new QuestionUpdateRequestDto(
                1L,
                "테스트질문내용수정",
                QuestionType.SINGLE_CHOICE
        );

        QuestionResponseDto responseDto = new QuestionResponseDto(
                1L,
                1L,
                "테스트질문내용수정",
                QuestionType.SINGLE_CHOICE
        );

        when(questionService.updateQuestion(eq(userId), eq(surveyId), eq(questionId), any(QuestionUpdateRequestDto.class)))
                .thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(patch("/api/survey/{surveyId}/question/{questionId}", surveyId, questionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        verify(questionService, times(1))
                .updateQuestion(eq(userId), eq(surveyId), eq(questionId), any(QuestionUpdateRequestDto.class));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value(requestDto.getContent()))
                .andDo(document("question/update-question",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("surveyId").description("설문 ID"),
                                parameterWithName("questionId").description("질문 ID")
                        ),
                        requestFields(
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("질문 번호"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("질문 유형")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("질문 번호"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("질문 유형")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_질문을_삭제한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYOR)
    void 질문을_삭제한다() throws Exception{
        Long surveyId = 1L;
        Long questionId = 1L;
        Long userId = 1L;

        doNothing().when(questionService).deleteQuestion(eq(userId), eq(surveyId), eq(questionId));

        ResultActions actions = mockMvc.perform(delete("/api/survey/{surveyId}/question/{questionId}", surveyId, questionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}"));

        verify(questionService, times(1))
                .deleteQuestion(eq(userId), eq(surveyId), eq(questionId));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(document("question/delete-question",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값)")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("surveyId").description("설문 ID"),
                                parameterWithName("questionId").description("질문 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 Data")
                        )
                ))
        ;
    }
}
