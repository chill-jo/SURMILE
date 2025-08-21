package com.example.surveyapp.domain.survey.infrastructure;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SurveyJpaRepository extends JpaRepository<Survey, Long> {

    Optional<Survey> findByIdAndIsDeletedFalse(Long id);

    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Survey s WHERE s.id = :id AND s.isDeleted = false")
    Optional<Survey> findByIdAndIsDeletedFalseWithPessimisticLock(@Param("id") Long id);

    @Query("SELECT s FROM Survey s WHERE s.isDeleted = false")
    Page<Survey> findAllSurveyPaged(Pageable pageable);

    @Query("SELECT s FROM Survey s "+
            "LEFT JOIN FETCH s.questions q " +
            "LEFT JOIN FETCH q.options o " +
            "WHERE s.id = :surveyId")
    Optional<Survey> findSurveyWithQuestionsAndOptions(@Param("surveyId") Long surveyId);

}
