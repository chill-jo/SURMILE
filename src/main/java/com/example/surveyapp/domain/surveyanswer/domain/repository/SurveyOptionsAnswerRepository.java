package com.example.surveyapp.domain.survey.domain.repository;

import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyOptionsAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SurveyOptionsAnswerRepository extends JpaRepository<SurveyOptionsAnswer, Long> {


}
