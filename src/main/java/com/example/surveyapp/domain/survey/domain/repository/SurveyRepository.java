package com.example.surveyapp.domain.survey.domain.repository;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Optional<Survey> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT s FROM Survey s WHERE s.isDeleted = false")
    Page<Survey> findAllSurveyPaged(Pageable pageable);

    @Query("SELECT s FROM Survey s "+
            "LEFT JOIN FETCH s.questions q " +
            "LEFT JOIN FETCH q.options o " +
            "WHERE s.id = :surveyId")
    Optional<Survey> findSurveyWithQuestionsAndOptions(@Param("surveyId") Long surveyId);

    boolean existByIdAndIsDeletedFalse(Long surveyId);
}
