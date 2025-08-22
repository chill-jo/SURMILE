package com.example.surveyapp.domain.point.infrastructure;

import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointWallet, Long> {
    Optional<PointWallet> findByUserId(Long userId);
    Boolean existsByUserId(Long userId);

    PointWallet save(PointWallet pointWallet);
}
