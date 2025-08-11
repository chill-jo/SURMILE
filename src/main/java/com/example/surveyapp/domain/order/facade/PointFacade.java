package com.example.surveyapp.domain.order.facade;

import com.example.surveyapp.domain.point.domain.model.entity.Points;

public interface PointFacade {
    //Point findPoint(Long id);

    //포인트 차감
    void decreasePoint (Long userId, Points amount, Long orderId);

}
