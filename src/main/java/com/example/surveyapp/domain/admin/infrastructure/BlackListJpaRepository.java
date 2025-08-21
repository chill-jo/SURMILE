package com.example.surveyapp.domain.admin.infrastructure;

import com.example.surveyapp.domain.admin.domain.model.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListJpaRepository extends JpaRepository<BlackList, Long> {

    Optional<BlackList> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

}
