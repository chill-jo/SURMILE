package com.example.surveyapp.domain.admin.application;

import com.example.surveyapp.config.generator.BlackListFixtureGenerator;
import com.example.surveyapp.domain.admin.application.facade.UserFacade;
import com.example.surveyapp.domain.admin.exception.AdminErrorCode;
import com.example.surveyapp.domain.admin.exception.AdminException;
import com.example.surveyapp.domain.admin.presentation.dto.StatDto;
import com.example.surveyapp.domain.admin.presentation.dto.StatsListDto;
import com.example.surveyapp.domain.admin.presentation.dto.UserDto;
import com.example.surveyapp.domain.admin.domain.model.BlackList;
import com.example.surveyapp.domain.admin.domain.repository.BlackListRepository;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("service: admin")
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserFacade userFacade;

    @Mock
    private BlackListRepository blackListRepository;

    @InjectMocks
    private AdminService adminService;

    private final BlackList blackList = BlackListFixtureGenerator.generateBlackListFixture();


    @Test
    @DisplayName("기능: 검색어를 조건으로 전체 회원을 조회한다")
    void success_getUserList() {

        // given
        Long userId = 1L;
        String search = "test";
        Pageable pageable = Pageable.unpaged();
        Page<UserDto> userList = Page.empty();

        when(userFacade.findAllBySearch(search,pageable)).thenReturn(userList);
        // when
        Page<UserDto> result = adminService.getUserList(search, pageable);

        // then
        Assertions.assertThat(userList).isEqualTo(result);

    }

    @Test
    @DisplayName("기능: 단일 회원을 조회한다")
    void success_getUser() {

        // given
        Long userId = 1L;
        UserDto dto = mock(UserDto.class);

        when(dto.getId()).thenReturn(userId);
        when(userFacade.getUserDto(userId)).thenReturn(dto);

        // when
        UserDto result = adminService.getUser(userId);

        // then
        Assertions.assertThat(result.getId()).isEqualTo(userId);

    }

    @Test
    @DisplayName("예외: 일치하는 회원이 없어서 단일 회원 조회를 실패한다")
    void fail_getUser() {

        // given
        Long userId = 1L;
        UserDto dto = mock(UserDto.class);

        //when(userFacade.getUserDto(userId)).thenReturn(dto);
        doThrow(new UserException(UserErrorCode.NOT_FOUND_USER))
                .when(userFacade).getUserDto(userId);

        // when & then
        assertThatThrownBy(() -> userFacade.getUserDto(userId))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.NOT_FOUND_USER.getMessage());

    }

    @Test
    @DisplayName("기능: 분류별 참여자 통계를 조회한다")
    void getStats() {

        //given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        StatsListDto listDto = StatsListDto.of("카테고리");
        Long count = 2L;

        StatDto statDto = new StatDto(1L, count);
        listDto.addStat(statDto);

        List<StatsListDto> expected = List.of(listDto);

        when(userFacade.getUserBaseDataStatistics(any(), any())).thenReturn(expected);

        //when
        List<StatsListDto> statsListDtoList = adminService.getStats(startDate, endDate);

        //then
        Assertions.assertThat(statsListDtoList.get(0).getList().get(0).getCount()).isEqualTo(2L);
    }


    @Test
    @DisplayName("기능: 블랙리스트에 등록한다")
    void success_addBlackList() {

        // given
        Long userId = 1L;
        UserDto dto = mock(UserDto.class);

        when(userFacade.getUserDto(userId)).thenReturn(dto);
        when(blackListRepository.findByUserId(userId)).thenReturn(null);
        when(blackListRepository.save(any(BlackList.class))).thenReturn(blackList);

        // when
        UserDto result = adminService.addBlackList(userId);

        // then
        Assertions.assertThat(result).isEqualTo(dto);

    }

    @Test
    @DisplayName("예외: 일치하는 회원이 없어서 블랙리스트 등록을 실패한다")
    void fail_addBlackList_1() {

        // given
        Long userId = 1L;

        doThrow(new UserException(UserErrorCode.NOT_FOUND_USER))
                .when(userFacade).getUserDto(userId);

        // when & then
        assertThatThrownBy(() -> adminService.addBlackList(userId))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.NOT_FOUND_USER.getMessage());

    }

    @Test
    @DisplayName("예외: 이미 블랙회원일 경우 블랙리스트 등록을 실패한다")
    void fail_addBlackList_2() {

        // given
        Long userId = 1L;
        UserDto userDto = mock(UserDto.class);
        when(userFacade.getUserDto(userId)).thenReturn(userDto);

        when(blackListRepository.findByUserId(userId)).thenReturn(blackList);

        // when & then
        assertThatThrownBy(() -> adminService.addBlackList(userId))
                .isInstanceOf(AdminException.class)
                .hasMessageContaining("해당 회원은 이미 블랙입니다.");

    }


    @Test
    @DisplayName("기능: 블랙리스트에서 삭제한다")
    void success_deleteBlackList() {

        // given
        Long userId = 1L;
        UserDto dto = mock(UserDto.class);
        when(userFacade.getUserDto(userId)).thenReturn(dto);
        when(blackListRepository.findByUserId(userId)).thenReturn(null);

        // when
        UserDto result = adminService.deleteBlackList(userId);


        // then
        Assertions.assertThat(result).isEqualTo(dto);

    }

    @Test
    @DisplayName("예외: 일치하는 회원이 없어서 블랙리스트 삭제를 실패한다")
    void fail_deleteBlackList_1() {

        // given
        Long userId = 1L;

        doThrow(new UserException(UserErrorCode.NOT_FOUND_USER))
                .when(userFacade).getUserDto(userId);

        // when & then
        assertThatThrownBy(() -> adminService.deleteBlackList(userId))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.NOT_FOUND_USER.getMessage());

    }

    @Test
    @DisplayName("예외: 블랙회원이 아니라서 블랙리스트 삭제를 실패한다")
    void fail_deleteBlackList_2() {

        // given
        Long userId = 1L;
        UserDto dto = mock(UserDto.class);
        when(userFacade.getUserDto(userId)).thenReturn(dto);

        doThrow(new AdminException(AdminErrorCode.IS_NOT_BLACKLIST))
                .when(blackListRepository).findByUserId(userId);


        // when & then
        assertThatThrownBy(() -> adminService.deleteBlackList(userId))
                .isInstanceOf(AdminException.class)
                .hasMessageContaining(AdminErrorCode.IS_NOT_BLACKLIST.getMessage());

    }
}