package com.example.surveyapp.domain.survey.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface QuestionJpaRepository extends JpaRepository<QuestionReadEntity, Long> {

    Page<QuestionReadEntity> findAllBySurveyId(@Param("surveyId") Long surveyId, Pageable pageable);
}
