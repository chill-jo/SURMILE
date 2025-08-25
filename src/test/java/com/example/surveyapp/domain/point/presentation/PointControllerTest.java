package com.example.surveyapp.domain.point.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.point.application.PointService;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.domain.payment.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.point.presentation.dto.response.PointHistoryResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("controller : PointController 테스트")
@WebMvcTest(controllers = PointController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class PointControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private PointService pointService;



    @Test
    @DisplayName("기능_포인트 내역을 조회한다.")
    @WithCustomMockUser(id = 1L, role = UserRoleEnum.SURVEYOR)
    void 포인트_내역_조회() throws Exception{

        Long userId = 1L;

        PointHistory pointHistory = PointHistory.of(
                PointBalance.of(5000L),
                PointBalance.of(5000L),
                PointBalance.of(10000L),
                PointType.CHARGE,
                Target.PAYMENTS,
                1L,
                "포인트 충전",
                userId,
                mock(PointWallet.class)
        );
        PointHistoryResponseDto responseDto = PointHistoryResponseDto.from(pointHistory);
        Page<PointHistoryResponseDto> pageResponseDto = new PageImpl<>(List.of(responseDto));

        when(pointService.getHistories(eq(userId), any(Pageable.class))).thenReturn(pageResponseDto);

        ResultActions actions = mockMvc.perform(get("/api/points")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .param("page", "0")
                .param("size", "10"));

        actions.andExpect(status().isOk())
                .andDo(document("point/get-point-history",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (기본값 0)").optional(),
                                parameterWithName("size").description("페이지 크기 (기본값 10)").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.content[].pointType").description("포인트 내역 타입"),
                                fieldWithPath("data.content[].amount").description("포인트 금액"),
                                fieldWithPath("data.content[].date").description("포인트 내역 생성일"),
                                fieldWithPath("data.pageable").type(JsonFieldType.STRING).description("요청한 페이지 관련 정보"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 건수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("현재 페이지가 마지막 페이지인지 여부"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("한 페이지에 보여지는 데이터 수"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호 (0부터 시작)"),
                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 기준이 비어있는지 여부"),
                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("데이터가 정렬되어 있는지 여부"),
                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("데이터가 정렬되지 않았는지 여부"),
                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지에 포함된 데이터 수"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("현재 페이지가 첫 페이지인지 여부"),
                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("현재 페이지에 데이터가 존재하지 않는지 여부"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;
    }

}
