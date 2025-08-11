package com.example.surveyapp.domain.point.service;

import com.example.surveyapp.domain.point.domain.model.entity.Money;
import com.example.surveyapp.domain.point.domain.model.entity.PointPoints;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyToPointService {

    public PointPoints convert(Money money){

        return PointPoints.create(money.getValue());
    }
}
