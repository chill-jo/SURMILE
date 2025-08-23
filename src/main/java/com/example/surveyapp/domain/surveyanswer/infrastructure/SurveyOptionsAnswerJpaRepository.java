package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyOptionsAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyOptionsAnswerJpaRepository extends JpaRepository<SurveyOptionsAnswer, Long> {
    Long countByQuestionIdAndNumber(Long questionId, Long number);
}
