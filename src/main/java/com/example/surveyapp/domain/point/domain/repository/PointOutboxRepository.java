package com.example.surveyapp.domain.point.domain.repository;

import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutboxEnum;

import java.util.List;

public interface PointOutboxRepository {

    PointOutbox save(PointOutbox pointOutbox);

    List<PointOutbox> findByStatusAndPublished(PointOutboxEnum status, boolean published);
}
