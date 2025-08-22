package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.payment.application.MoneyToPointService;
import com.example.surveyapp.domain.payment.domain.model.vo.Money;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.example.surveyapp.domain.payment.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.point.presentation.dto.response.PointHistoryResponseDto;
import com.example.surveyapp.domain.payment.domain.model.entity.Payment;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.payment.domain.repository.PaymentRepository;
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