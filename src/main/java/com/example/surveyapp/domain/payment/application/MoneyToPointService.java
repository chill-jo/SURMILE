package com.example.surveyapp.domain.payment.application;

import com.example.surveyapp.domain.payment.domain.model.vo.Money;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyToPointService {

    public PointBalance convert(Money money){

        return PointBalance.of(money.getValue());
    }
}
