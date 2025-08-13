package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.domain.model.entity.vo.Money;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.example.surveyapp.domain.point.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.point.presentation.dto.response.PointHistoryResponseDto;
import com.example.surveyapp.domain.point.domain.model.entity.Payment;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.global.reader.UserReader;
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
    private MoneyToPointService moneyToPointService;

    @Mock
    private UserReader userReader;

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

        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);

        when(dto.getPrice()).thenReturn(chargeAmount);

        Money amount = Money.krw(chargeAmount);
        PointBalance chargePoint = PointBalance.of(chargeAmount);

        when(moneyToPointService.convert(any(Money.class))).thenReturn(chargePoint);

        PointWallet pointMock = mock(PointWallet.class);
        PointBalance currentBalance = PointBalance.of(2000L);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(pointMock));

        when(pointMock.getPointBalance()).thenReturn(currentBalance);
        doNothing().when(pointMock).pointCharge(any(PointBalance.class));

        Payment payment = mock(Payment.class);
        PointHistory pointHistory = mock(PointHistory.class);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        // when
        pointService.charge(userId,dto);

        // then
        verify(pointMock).pointCharge(any(PointBalance.class));
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
        when(dto.getPrice()).thenReturn(chargeAmount);

        Money amount = Money.krw(chargeAmount);
        PointBalance chargePoint = PointBalance.of(chargeAmount);

        when(moneyToPointService.convert(any(Money.class))).thenReturn(chargePoint);

        PointWallet point = mock(PointWallet.class);
        PointBalance currentBalance = PointBalance.of(2000L);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        doThrow(new PointException(PointErrorCode.POINT_INVALID_AMOUNT))
                .when(point).pointCharge(any(PointBalance.class));


        // when / then
        assertThatThrownBy(() -> pointService.charge(userId,dto))
                .isInstanceOf(PointException.class)
                .hasMessage(PointErrorCode.POINT_INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("예외_테스트_출제자_포인트_충전_요청_값이_null인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생")
    void 출제자_포인트_충전_요청_값이_null인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생(){
        // given
        Long userId = 1L;
        Long chargeAmount = null;

        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);

        when(dto.getPrice()).thenReturn(chargeAmount);

        Money amount = Money.krw(chargeAmount);
        doThrow(new PointException(PointErrorCode.POINT_INVALID_AMOUNT))
                .when(moneyToPointService).convert(any(Money.class));

        // when / then
        assertThatThrownBy(() -> pointService.charge(userId, dto))
                .isInstanceOf(PointException.class)
                .hasMessage(PointErrorCode.POINT_INVALID_AMOUNT.getMessage());
    }


    @Test
    @DisplayName("기능_테스트_출제자_포인트_내역_조회를_성공한다")
    void 출제자_포인트_내역_조회를_성공한다() {
        //given
        Long userId = 1L;
        Pageable pageable =PageRequest.of(0, 10);

        PointHistory mockHistory1 = PointHistory.of(
                PointBalance.of(100L),
                PointBalance.of(200L),
                PointBalance.of(300L),
                PointType.CHARGE,
                Target.PAYMENTS,
                1L,
                "테스트포인트내역",
                userId,
                mock(PointWallet.class)
        );
        PointHistory mockHistory2 = PointHistory.of(
                PointBalance.of(100L),
                PointBalance.of(200L),
                PointBalance.of(300L),
                PointType.CHARGE,
                Target.PAYMENTS,
                1L,
                "테스트포인트내역",
                userId,
                mock(PointWallet.class)
        );

        List<PointHistory> histories = List.of(
                mockHistory1,
                mockHistory2
        );
        Page<PointHistory> page = new PageImpl<>(histories, pageable, histories.size());

        when(pointHistoryRepository.findPointHistoryByUserId(userId, pageable)).thenReturn(page);

        // when
        Page<PointHistoryResponseDto> result = pointService.getHistories(userId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(histories.size());
        assertThat(result.getContent()).hasSize(2);

    }

}