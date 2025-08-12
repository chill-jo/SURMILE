package com.example.surveyapp.domain.survey.domain.repository;

import com.example.surveyapp.domain.survey.infrastructure.QuestionReadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionReadEntity, Long> {

    //Page<QuestionReadEntity> findAllBySurveyId(@Param("surveyId") Long surveyId, Pageable pageable);
}