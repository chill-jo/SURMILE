package com.example.surveyapp.domain.point.infrastructure;

import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointWallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PointWallet> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

    PointWallet save(PointWallet pointWallet);
}
