package com.example.surveyapp.domain.point.domain.repository;

import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<PointWallet, Long> {
    Optional<PointWallet> findByUserId(Long userId);
}
