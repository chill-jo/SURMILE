package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyTextAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyTextAnswerJpaRepository extends JpaRepository<SurveyTextAnswer, Long> {
}
