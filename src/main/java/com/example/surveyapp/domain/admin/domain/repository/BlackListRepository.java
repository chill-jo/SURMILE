package com.example.surveyapp.domain.admin.domain.repository;

import com.example.surveyapp.domain.admin.domain.model.BlackList;
import com.example.surveyapp.domain.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    Optional<BlackList> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}