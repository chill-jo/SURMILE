package com.example.surveyapp.domain.point.infrastructure;

import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutboxEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointOutboxJpaRepository extends JpaRepository<PointOutbox, Long> {

    List<PointOutbox> findByStatusAndPublished(PointOutboxEnum status, boolean published);
}
