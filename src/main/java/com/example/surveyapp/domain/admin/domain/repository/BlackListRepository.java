package com.example.surveyapp.domain.admin.domain.repository;

import com.example.surveyapp.domain.admin.domain.model.BlackList;

import java.util.Optional;

public interface BlackListRepository {

    Optional<BlackList> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    BlackList save(BlackList blackList);
    void delete(BlackList blackList);
}