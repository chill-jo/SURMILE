package com.example.surveyapp.domain.user.domain.repository;

import org.springframework.stereotype.Repository;

@Repository
public class UserR2dbcRepository {
    public boolean existsByEmail(String email) {
        return true;
    }
}
