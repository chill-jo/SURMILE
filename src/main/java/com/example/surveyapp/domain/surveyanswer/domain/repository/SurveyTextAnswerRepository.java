package com.example.surveyapp.domain.surveyanswer.domain.repository;

import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyTextAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyTextAnswerRepository extends JpaRepository<SurveyTextAnswer, Long> {
}
