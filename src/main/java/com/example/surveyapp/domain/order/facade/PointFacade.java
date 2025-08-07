package com.example.surveyapp.domain.order.facade;

import com.example.surveyapp.domain.point.domain.model.entity.Point;

public interface PointFacade {
    Point findPoint(Long id);
}
