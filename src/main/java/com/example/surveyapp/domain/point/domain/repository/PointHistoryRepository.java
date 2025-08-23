package com.example.surveyapp.domain.point.domain.repository;

import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryRepository {
    Page<PointHistory> findPointHistoryByUserId (Long userId, Pageable pageable);

    PointHistory save(PointHistory history);
}
