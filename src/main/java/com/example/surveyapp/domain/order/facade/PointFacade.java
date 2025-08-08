package com.example.surveyapp.domain.order.facade;

import com.example.surveyapp.domain.point.domain.model.entity.Point;

public interface PointFacade {
    //Point findPoint(Long id);

    //포인트 차감
    void decreasePoint (Long userId, Long amount, Long orderId);

}
