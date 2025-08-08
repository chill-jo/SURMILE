package com.example.surveyapp.domain.admin.service;

import com.example.surveyapp.config.generator.BlackListFixtureGenerator;
import com.example.surveyapp.config.generator.UserFixtureGenerator;
import com.example.surveyapp.domain.admin.controller.dto.StatsListDto;
import com.example.surveyapp.domain.admin.controller.dto.UserDto;
import com.example.surveyapp.domain.admin.domain.model.BlackList;
import com.example.surveyapp.domain.admin.domain.repository.BlackListRepository;
import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserBaseDataRepository;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("service: admin")
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    UserBaseDataRepository userBaseDataRepository;

    @Mock
    private UserDto userDto;

    @Mock
    private BlackListRepository blackListRepository;

    @InjectMocks
    private AdminService adminService;

    private final User user = UserFixtureGenerator.generateUserFixture();

    private final BlackList blackList = BlackListFixtureGenerator.generateBlackListFixture();


    @Test
    @DisplayName("기능: 검색어를 조건으로 전체 회원을 조회한다")
    void success_getUserList() {

        // given
        String search = "test";
        Pageable pageable = Pageable.unpaged();
        Page<UserDto> userList = Page.empty();
        when(userRepository.findAllBySearch(search, pageable)).thenReturn(userList);

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
        ReflectionTestUtils.setField(user, "id", userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

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

        // when & then
        Assertions.assertThatThrownBy(() -> adminService.getUser(userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("기능: 분류별 참여자 통계를 조회한다")
    void getStats() {

        //given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        Long count = 1L;
        when(userBaseDataRepository.countByCategoryAndDataAndStartDateAndEndDate(any(CategoryEnum.class),
                any(Long.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(count);

        //when
        List<StatsListDto> statsListDtoList = adminService.getStats(startDate, endDate);

        //then
        Assertions.assertThat(statsListDtoList.get(0).getList().get(0).getCount()).isEqualTo(1L);
    }


    @Test
    @DisplayName("기능: 블랙리스트에 등록한다")
    void success_addBlackList() {

        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = adminService.addBlackList(userId);

        // then
        Assertions.assertThat(user).isEqualTo(result);

    }

    @Test
    @DisplayName("예외: 일치하는 회원이 없어서 블랙리스트 등록을 실패한다")
    void fail_addBlackList_1() {

        // given
        Long userId = 1L;

        // when & then
        Assertions.assertThatThrownBy(() -> adminService.addBlackList(userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("예외: 이미 블랙회원일 경우 블랙리스트 등록을 실패한다")
    void fail_addBlackList_2() {

        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(blackListRepository.findByUserId(userId)).thenReturn(Optional.of(blackList));

        // when & then
        Assertions.assertThatThrownBy(() -> adminService.addBlackList(userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("해당 회원은 이미 블랙입니다.");

    }


    @Test
    @DisplayName("기능: 블랙리스트에서 삭제한다")
    void success_deleteBlackList() {

        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(blackListRepository.findByUserId(userId)).thenReturn(Optional.of(blackList));

        // when
        User result = adminService.deleteBlackList(userId);


        // then
        Assertions.assertThat(user).isEqualTo(result);

    }

    @Test
    @DisplayName("예외: 일치하는 회원이 없어서 블랙리스트 삭제를 실패한다")
    void fail_deleteBlackList_1() {

        // given
        Long userId = 1L;

        // when & then
        Assertions.assertThatThrownBy(() -> adminService.deleteBlackList(userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("예외: 블랙회원이 아니라서 블랙리스트 삭제를 실패한다")
    void fail_deleteBlackList_2() {

        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when & then
        Assertions.assertThatThrownBy(() -> adminService.deleteBlackList(userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("해당 회원은 블랙이 아닙니다.");

    }
}