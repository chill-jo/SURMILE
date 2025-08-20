package com.example.surveyapp.domain.survey.domain.repository;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyRepository {

    Survey save(Survey survey);

    Survey findByIdAndIsDeletedFalse(Long id);

    Survey findByIdAndIsDeletedFalseWithPessimisticLock(@Param("id") Long id);

    Page<Survey> findAllSurveyPaged(Pageable pageable);

    Survey findSurveyWithQuestionsAndOptions(@Param("surveyId") Long surveyId);

}
