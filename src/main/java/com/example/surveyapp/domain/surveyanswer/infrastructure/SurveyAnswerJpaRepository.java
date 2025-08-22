package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyAnswerJpaRepository extends JpaRepository<SurveyAnswer, Long> {

    List<SurveyAnswer> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsBySurveyIdAndUserId(Long surveyId, Long userId);

    Long countBySurveyId(Long surveyId);

}
