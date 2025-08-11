package com.example.surveyapp.domain.point.service;

import com.example.surveyapp.domain.point.domain.model.entity.Money;
import com.example.surveyapp.domain.point.domain.model.entity.Points;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyToPointService {

    public Points convert(Money money){

        return Points.of(money.getValue());
    }
}
