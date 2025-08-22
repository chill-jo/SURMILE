package com.example.surveyapp.domain.point.infrastructure;

import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Page<PointHistory> findPointHistoryByUserId(Long userId, Pageable pageable) {
        return pointHistoryJpaRepository.findPointHistoryByUserId(userId, pageable);
    }

    @Override
    public PointHistory save(PointHistory history) {
        return pointHistoryJpaRepository.save(history);
    }
}
