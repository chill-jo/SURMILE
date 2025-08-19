package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.domain.event.PointRedeemSuccessEvent;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@DisplayName("Service: PointEarnReddemService 테스트")
@ExtendWith(MockitoExtension.class)
public class PointEarnRedeemServiceTest {
    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PointEarnRedeemService pointEarnRedeemService;

    @Test
    @DisplayName("기능_테스트_출제자_설문생성시_포인트가_차감된다")
    void 출제자_설문생성시_포인트가_차감된다() {
        // given
        Long userId = 1L;
        PointBalance amount = PointBalance.of(2000L);
        Long surveyId = 100L;

        PointWallet point = mock(PointWallet.class);
        PointBalance currentBalance = PointBalance.of(10000L);
        PointRedeemSuccessEvent event = new PointRedeemSuccessEvent(userId, surveyId);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(point.getPointBalance()).thenReturn(currentBalance);

        doNothing().when(point).redeem(amount);

        when(point.getPointBalance()).thenReturn(PointBalance.of(8000L));

        PointHistory pointHistory = mock(PointHistory.class);
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        // when
        pointEarnRedeemService.decreaseSurveyorPoint(userId, amount, surveyId);

        // then
        assertThat(point.getPointBalance().getValue()).isEqualTo(8000L);

        verify(eventPublisher).publishEvent(any(PointRedeemSuccessEvent.class));
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("예외_테스트_출제자_설문생성시_차감_포인트보다_부족한_경우_예외_발생")
    void 출제자_설문생성시_차감_포인트보다_부족한_경우_예외_발생() {
        // given
        Long userId = 1L;
        PointBalance amount = PointBalance.of(7000L);
        Long surveyId = 100L;

        PointWallet point = mock(PointWallet.class);
        PointBalance currentBalance = PointBalance.of(5000L);
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        doThrow(new PointException(PointErrorCode.POINT_NOT_ENOUGH))
                .when(point).redeem(amount);

        // when / then
        assertThatThrownBy(() -> pointEarnRedeemService.decreaseSurveyorPoint(userId, amount, surveyId))
                .isInstanceOf(PointException.class)
                .hasMessage(PointErrorCode.POINT_NOT_ENOUGH.getMessage());

        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("기능_테스트_참여자_설문_응답시_포인트를_지급받는다")
    void 참여자_설문_응답시_포인트를_지급받는다() {
        // given
        Long userId = 1L;
        PointBalance amount = PointBalance.of(2000L);
        Long surveyAnswerId = 500L;

        PointWallet point = mock(PointWallet.class);
        PointBalance currentBalance = PointBalance.of(1000L);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(point.getPointBalance()).thenReturn(currentBalance);
        doNothing().when(point).earn(amount);

        when(point.getPointBalance()).thenReturn(PointBalance.of(3000L));
        PointHistory pointHistory = mock(PointHistory.class);
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        // when
        pointEarnRedeemService.increaseSurveyeePoint(userId, amount, surveyAnswerId);

        // then
        assertThat(point.getPointBalance().getValue()).isEqualTo(3000L);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("기능_테스트_참여자_상점_상품_교환시_상품_가격만큼_포인트가_차감된다")
    void 참여자_상점_상품_교환시_상품_가격만큼_포인트가_차감된다() {
        // given
        Long userId = 1L;
        PointBalance amount = PointBalance.of(4000L);
        Long orderId = 10L;

        PointWallet point = mock(PointWallet.class);
        PointBalance currentBalance = PointBalance.of(5000L);
        when(point.getPointBalance()).thenReturn(currentBalance);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        doNothing().when(point).redeem(amount);
        when(point.getPointBalance()).thenReturn(PointBalance.of(1000L));
        PointHistory pointHistory = mock(PointHistory.class);
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        // when
        pointEarnRedeemService.decreasePoint(userId, amount, orderId);

        // then
        assertThat(point.getPointBalance().getValue()).isEqualTo(1000L);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }


    @Test
    @DisplayName("예외_테스트_참여자_상점_상품_교환시_보유_포인트가_부족한_경우_POINT_NOT_ENOUGH_커스텀_예외_발생")
    void 참여자_상점_상품_교환시_보유_포인트가_부족한_경우_POINT_NOT_ENOUGH_커스텀_예외_발생() {
        // given
        Long userId = 1L;
        PointBalance amount = PointBalance.of(10000L);
        Long orderId = 10L;

        PointWallet point = mock(PointWallet.class);
        PointBalance currentBalance = PointBalance.of(5000L);
        when(point.getPointBalance()).thenReturn(currentBalance);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        doThrow(new PointException(PointErrorCode.POINT_NOT_ENOUGH))
                .when(point).redeem(amount);

        // when / then
        assertThatThrownBy(() -> pointEarnRedeemService.decreasePoint(userId, amount, orderId))
                .isInstanceOf(PointException.class)
                .hasMessage(PointErrorCode.POINT_NOT_ENOUGH.getMessage());

        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }


}
