package com.example.surveyapp.domain.point.service;

import com.example.surveyapp.config.generator.PointFixtureGenerator;
import com.example.surveyapp.domain.point.controller.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.point.controller.dto.response.PointHistoryResponseDto;
import com.example.surveyapp.domain.point.domain.model.entity.Payment;
import com.example.surveyapp.domain.point.domain.model.entity.Point;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import static com.example.surveyapp.global.response.exception.ErrorCode.POINT_INVALID_AMOUNT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

@DisplayName("Service: Points 테스트")
@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PointService pointService;


    @Test
    @DisplayName("기능_테스트_출제자_포인트를_충전한다")
    void 출제자_포인트를_충전한다(){
        // given
        Long userId = 1L;
        Long chargeAmount = 10000L;
        User userMock=mock(User.class);
        Point pointMock=mock(Point.class);
        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userMock));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(pointMock));
        when(dto.getPrice()).thenReturn(chargeAmount);

        // when
        pointService.charge(userId,dto);

        // then
        verify(pointMock).pointCharge(chargeAmount);
        verify(paymentRepository).save(any(Payment.class));
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("예외_테스트_출제자_포인트_충전_요청_값이_5000원_미만인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생")
    void 출제자_포인트_충전_요청_값이_5000원_미만인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생(){
        // given
        Long userId = 1L;
        Long chargeAmount = 4999L;

        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);
        User mockUser = mock(User.class);

        Point point = PointFixtureGenerator.generatePointFixture(mockUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(dto.getPrice()).thenReturn(chargeAmount);

        // when / then
        assertThatThrownBy(() -> pointService.charge(userId,dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(POINT_INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("예외_테스트_출제자_포인트_충전_요청_값이_null인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생")
    void 출제자_포인트_충전_요청_값이_null인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생(){
        // given
        Long userId = 1L;
        Long chargeAmount = null;

        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);
        User mockUser = mock(User.class);

        Point point = PointFixtureGenerator.generatePointFixture(mockUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(dto.getPrice()).thenReturn(chargeAmount);

        // when / then
        assertThatThrownBy(() -> pointService.charge(userId, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(POINT_INVALID_AMOUNT.getMessage());

    }

    @Test
    @DisplayName("기능_테스트_출제자_설문생성시_포인트가_차감된다")
    void 출제자_설문생성시_포인트가_차감된다() {
        // given
        Long userId = 1L;
        Long amount = 3000L;
        Long surveyId = 100L;

        User user = mock(User.class);
        Point point = spy(PointFixtureGenerator.generatePointFixture(user, 5000L));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // when
        pointService.surveyorRedeem(userId, amount, surveyId);

        // then
        assertThat(point.getPointBalance()).isEqualTo(2000L);

        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }


    @Test
    @DisplayName("예외_테스트_출제자_설문생성시_차감_포인트보다_부족한_경우_예외_발생")
    void 출제자_설문생성시_차감_포인트보다_부족한_경우_예외_발생() {
        // given
        Long userId = 1L;
        Long amount = 7000L;
        Long surveyId = 100L;

        User user = mock(User.class);
        Point point = spy(PointFixtureGenerator.generatePointFixture(user, 5000L));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // when / then
        assertThatThrownBy(() -> pointService.surveyorRedeem(userId, amount, surveyId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.POINT_NOT_ENOUGH.getMessage());

        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("기능_테스트_출제자_포인트_내역_조회를_성공한다")
    void 출제자_포인트_내역_조회를_성공한다() {
        //given
        Long userId = 1L;
        Pageable pageable =PageRequest.of(0, 10);

        User user = mock(User.class);

        List<PointHistory> histories = List.of(
                mock(PointHistory.class),
                mock(PointHistory.class)
        );
        Page<PointHistory> page = new PageImpl<>(histories, pageable, histories.size());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointHistoryRepository.findPointHistoryByUser(user, pageable)).thenReturn(page);

        // when
        Page<PointHistoryResponseDto> result = pointService.getHistories(userId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(histories.size());
        assertThat(result.getContent()).hasSize(2);

    }
    @Test
    @DisplayName("예외_테스트_출제자_포인트_내역_조회를_실패한다")
    void 출제자_포인트_내역_조회를_실패한다() {
        // given
        Long userId = 1L;
        Pageable pageable =PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> pointService.getHistories(userId, pageable))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage());
    }

    @Test
    @DisplayName("기능_테스트_참여자_설문_응답시_포인트를_지급받는다")
    void 참여자_설문_응답시_포인트를_지급받는다() {
        // given
        Long userId = 1L;
        Long amount = 2000L;
        Long surveyAnswerId = 500L;

        User user = mock(User.class);
        Point point = spy(PointFixtureGenerator.generatePointFixture(user, 5000L));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // when
        pointService.earn(userId, amount, surveyAnswerId);

        // then
        assertThat(point.getPointBalance()).isEqualTo(7000L);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("기능_테스트_참여자_상점_상품_교환시_상품_가격만큼_포인트가_차감된다")
    void 참여자_상점_상품_교환시_상품_가격만큼_포인트가_차감된다() {
        // given
        Long userId = 1L;
        Long amount = 4000L;
        Long orderId = 10L;

        User user = mock(User.class);

        Point point = spy(PointFixtureGenerator.generatePointFixture(user, 10000L));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // when
        pointService.redeem(userId, amount, orderId);

        // then
        assertThat(point.getPointBalance()).isEqualTo(6000L);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }


    @Test
    @DisplayName("예외_테스트_참여자_상점_상품_교환시_보유_포인트가_부족한_경우_POINT_NOT_ENOUGH_커스텀_예외_발생")
    void 참여자_상점_상품_교환시_보유_포인트가_부족한_경우_POINT_NOT_ENOUGH_커스텀_예외_발생() {
        // given
        Long userId = 1L;
        Long amount = 10000L;
        Long orderId = 10L;

        User user = mock(User.class);
        Point point = spy(PointFixtureGenerator.generatePointFixture(user, 5000L));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // when / then
        assertThatThrownBy(() -> pointService.redeem(userId, amount, orderId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.POINT_NOT_ENOUGH.getMessage());

        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }
}