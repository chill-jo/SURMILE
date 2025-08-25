package com.example.surveyapp.domain.admin.presentation;

import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.admin.application.AdminService;
import com.example.surveyapp.domain.admin.presentation.dto.StatDto;
import com.example.surveyapp.domain.admin.presentation.dto.StatsListDto;
import com.example.surveyapp.domain.admin.presentation.dto.UserDto;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("controller : AdminController 테스트")
@WebMvcTest(controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class AdminControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    @DisplayName("기능_전체 회원을 조회한다.")
    @WithCustomMockUser(id = 1L, role = UserRoleEnum.ADMIN)
    void 전체회원_조회() throws Exception{

        UserDto responseDto1 = new UserDto(
                1L,
                "email1.com",
                "테스트이름1",
                "테스트닉네임1",
                UserRoleEnum.ADMIN,
                false
        );
        UserDto responseDto2 = new UserDto(
                2L,
                "email2.com",
                "테스트이름2",
                "테스트닉네임2",
                UserRoleEnum.SURVEYEE,
                false
        );
        Page<UserDto> pageUserDto = new PageImpl<>(List.of(responseDto1, responseDto2));

        when(adminService.getUserList(anyString(), any(Pageable.class))).thenReturn(pageUserDto);

        ResultActions actions = mockMvc.perform(get("/api/admin/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .param("search", "kim")
                .param("page", "0")
                .param("size", "10"));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$.data.content[1].id").value(responseDto2.getId()))
                .andDo(document("admin/get-users",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("search").description("검색내용").optional(),
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.content[].id").description("회원 ID"),
                                fieldWithPath("data.content[].name").description("회원 이름"),
                                fieldWithPath("data.content[].nickname").description("회원 닉네임"),
                                fieldWithPath("data.content[].email").description("회원 이메일"),
                                fieldWithPath("data.content[].userRole").description("회원 역할"),
                                fieldWithPath("data.content[].isDeleted").description("회원 삭제여부"),
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

    @Test
    @DisplayName("기능_단일 회원을 조회한다.")
    @WithCustomMockUser(id = 1L, role = UserRoleEnum.ADMIN)
    void 단일_회원_조회() throws Exception{

        Long userId = 2L;

        UserDto responseDto = new UserDto(
                2L,
                "email2.com",
                "테스트이름2",
                "테스트닉네임2",
                UserRoleEnum.SURVEYEE,
                false
        );

        when(adminService.getUser(anyLong())).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(get("/api/admin/users/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}"));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andDo(document("admin/get-user",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.id").description("회원 ID"),
                                fieldWithPath("data.name").description("회원 이름"),
                                fieldWithPath("data.nickname").description("회원 닉네임"),
                                fieldWithPath("data.email").description("회원 이메일"),
                                fieldWithPath("data.userRole").description("회원 역할"),
                                fieldWithPath("data.isDeleted").description("회원 삭제여부"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_참여자 통계를 조회한다.")
    @WithCustomMockUser(id = 1L, role = UserRoleEnum.ADMIN)
    void 참여자_통계_조회() throws Exception{

        StatDto statDto1 = new StatDto(
                1L,
                10L
        );
        StatDto statDto2 = new StatDto(
                2L,
                20L
        );
        StatsListDto statsListDto = new StatsListDto(
                "질문제목"
        );

        statsListDto.addStat(statDto1);
        statsListDto.addStat(statDto2);

        List<StatsListDto> statsListDtos = List.of(statsListDto);

        when(adminService.getStats(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(statsListDtos);

        ResultActions actions = mockMvc.perform(get("/api/admin/stats")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .param("startDate", "2025-08-01 00:00:00")
                .param("endDate", "2025-08-31 23:59:59"));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].list[0].option").value(statDto1.getOption()))
                .andExpect(jsonPath("$.data[0].list[1].option").value(statDto2.getOption()))
                .andExpect(jsonPath("$.data[0].list[0].count").value(statDto1.getCount()))
                .andExpect(jsonPath("$.data[0].list[1].count").value(statDto2.getCount()))
                .andDo(document("admin/get-stats",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data[].question").type(JsonFieldType.STRING).description("질문 제목"),
                                fieldWithPath("data[].list[].option").type(JsonFieldType.NUMBER).description("질문지 내용"),
                                fieldWithPath("data[].list[].count").type(JsonFieldType.NUMBER).description("질문지 선택 수"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_유저를 블랙리스트에 등록한다.")
    @WithCustomMockUser(id = 1L, role = UserRoleEnum.ADMIN)
    void 블랙리스트에_등록한다() throws Exception{
        Long userId = 2L;

        UserDto responseDto = new UserDto(
                2L,
                "email2.com",
                "테스트이름2",
                "테스트닉네임2",
                UserRoleEnum.SURVEYEE,
                false
        );

        when(adminService.addBlackList(userId)).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/admin/black/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}"));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andDo(document("admin/add-blacklist",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.id").description("회원 ID"),
                                fieldWithPath("data.name").description("회원 이름"),
                                fieldWithPath("data.nickname").description("회원 닉네임"),
                                fieldWithPath("data.email").description("회원 이메일"),
                                fieldWithPath("data.userRole").description("회원 역할"),
                                fieldWithPath("data.isDeleted").description("회원 삭제여부"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기능_유저를 블랙리스트에서 삭제한다.")
    @WithCustomMockUser(id = 1L, role = UserRoleEnum.ADMIN)
    void 블랙리스트에서_삭제한다() throws Exception{
        Long userId = 2L;

        UserDto responseDto = new UserDto(
                2L,
                "email2.com",
                "테스트이름2",
                "테스트닉네임2",
                UserRoleEnum.SURVEYEE,
                false
        );

        when(adminService.deleteBlackList(userId)).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(delete("/api/admin/black/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}"));

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andDo(document("admin/delete-blacklist",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.id").description("회원 ID"),
                                fieldWithPath("data.name").description("회원 이름"),
                                fieldWithPath("data.nickname").description("회원 닉네임"),
                                fieldWithPath("data.email").description("회원 이메일"),
                                fieldWithPath("data.userRole").description("회원 역할"),
                                fieldWithPath("data.isDeleted").description("회원 삭제여부"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )
                ))
        ;
    }
}
