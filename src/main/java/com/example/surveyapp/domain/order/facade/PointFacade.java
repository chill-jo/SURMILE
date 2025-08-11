package com.example.surveyapp.domain.order.facade;

import com.example.surveyapp.domain.point.domain.model.entity.PointPoints;

public interface PointFacade {
    //포인트 차감
    void decreasePoint (Long userId, PointPoints amount, Long orderId);

}