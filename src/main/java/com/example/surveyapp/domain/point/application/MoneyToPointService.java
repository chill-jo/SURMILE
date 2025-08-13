package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.domain.model.entity.vo.Money;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyToPointService {

    public PointBalance convert(Money money){

        return PointBalance.of(money.getValue());
    }
}
