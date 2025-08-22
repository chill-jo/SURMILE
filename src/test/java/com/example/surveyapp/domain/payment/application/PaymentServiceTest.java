package com.example.surveyapp.domain.payment.application;

import com.example.surveyapp.domain.payment.domain.model.entity.Payment;
import com.example.surveyapp.domain.payment.domain.model.vo.Money;
import com.example.surveyapp.domain.payment.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.payment.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.payment.presentation.dto.response.PointChargeResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Service: Payment 테스트")
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("기능_테스트_출제자_포인트를_충전한다")
    void 출제자_포인트를_충전한다(){
        // given
        Long userId = 1L;
        Long chargeAmount = 10000L;
        Payment payment = mock(Payment.class);
        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);

        when(dto.getPrice()).thenReturn(chargeAmount);

        Money amount = Money.krw(chargeAmount);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        PointChargeResponseDto responseDto = paymentService.charge(userId,dto);

        // then
        verify(paymentRepository).save(any(Payment.class));
    }

//    @Test
//    @DisplayName("예외_테스트_출제자_포인트_충전_요청_값이_5000원_미만인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생")
//    void 출제자_포인트_충전_요청_값이_5000원_미만인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생(){
//        // given
//        Long userId = 1L;
//        Long chargeAmount = 4999L;
//
//        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);
//        when(dto.getPrice()).thenReturn(chargeAmount);
//
//        Money amount = Money.krw(chargeAmount);
//        PointBalance chargePoint = PointBalance.of(chargeAmount);
//
//        when(moneyToPointService.convert(any(Money.class))).thenReturn(chargePoint);
//
//        PointWallet point = mock(PointWallet.class);
//        PointBalance currentBalance = PointBalance.of(2000L);
//
//        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
//        doThrow(new PointException(PointErrorCode.POINT_INVALID_AMOUNT))
//                .when(point).pointCharge(any(PointBalance.class));
//
//
//        // when / then
//        assertThatThrownBy(() -> pointService.charge(userId,dto))
//                .isInstanceOf(PointException.class)
//                .hasMessage(PointErrorCode.POINT_INVALID_AMOUNT.getMessage());
//    }
//
//    @Test
//    @DisplayName("예외_테스트_출제자_포인트_충전_요청_값이_null인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생")
//    void 출제자_포인트_충전_요청_값이_null인_경우_POINT_INVALID_AMOUNT_커스템_예외_발생(){
//        // given
//        Long userId = 1L;
//        Long chargeAmount = null;
//
//        PointChargeRequestDto dto = mock(PointChargeRequestDto.class);
//
//        when(dto.getPrice()).thenReturn(chargeAmount);
//
//        Money amount = Money.krw(chargeAmount);
//        doThrow(new PointException(PointErrorCode.POINT_INVALID_AMOUNT))
//                .when(moneyToPointService).convert(any(Money.class));
//
//        // when / then
//        assertThatThrownBy(() -> pointService.charge(userId, dto))
//                .isInstanceOf(PointException.class)
//                .hasMessage(PointErrorCode.POINT_INVALID_AMOUNT.getMessage());
//    }

}
