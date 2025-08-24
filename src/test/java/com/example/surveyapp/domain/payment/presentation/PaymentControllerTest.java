package com.example.surveyapp.domain.payment.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.payment.application.PaymentService;
import com.example.surveyapp.domain.payment.domain.model.enums.Method;
import com.example.surveyapp.domain.payment.domain.model.enums.PointStatus;
import com.example.surveyapp.domain.payment.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.payment.presentation.dto.response.PointChargeResponseDto;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DisplayName("controller : PaymentController 테스트")
@WebMvcTest(controllers = PaymentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class PaymentControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("기능_포인트를 충전한다.")
    @WithCustomMockUser(id = 1L, role = UserRoleEnum.SURVEYOR)
    void 포인트를_충전한다() throws Exception{

        Long userId = 1L;
        PointChargeRequestDto requestDto = new PointChargeRequestDto(5000L);
        PointChargeResponseDto responseDto = new PointChargeResponseDto(
                1L,
                10000L,
                PointStatus.PENDING,
                Method.KAKAO_PAY
        );

        when(paymentService.charge(anyLong(), any(PointChargeRequestDto.class))).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/payment/charge")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        actions.andExpect(status().isCreated())
                .andDo(document("payment/charge-point",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        requestFields(
                                fieldWithPath("price").description("충전 금액")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.paymentId").type(JsonFieldType.NUMBER).description("payment ID"),
                                fieldWithPath("data.amount").type(JsonFieldType.NUMBER).description("충전 금액"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("충전 결제 상태"),
                                fieldWithPath("data.method").type(JsonFieldType.STRING).description("충전 결제 방법"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;

        verify(paymentService).charge(anyLong(), any(PointChargeRequestDto.class));

    }
}
