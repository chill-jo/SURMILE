package com.example.surveyapp.domain.point.infrastructure;

import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutboxEnum;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PointOutboxRepositoryImpl implements PointOutboxRepository {

    private final PointOutboxJpaRepository pointOutboxJpaRepository;

    public PointOutbox save(PointOutbox pointOutbox){
        return pointOutboxJpaRepository.save(pointOutbox);
    }

    @Override
    public List<PointOutbox> findByStatusAndPublished(PointOutboxEnum status, boolean published) {
        return pointOutboxJpaRepository.findByStatusAndPublished(status,published);
    }

}
