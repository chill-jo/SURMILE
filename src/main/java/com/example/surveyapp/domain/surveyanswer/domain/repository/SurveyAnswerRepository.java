package com.example.surveyapp.domain.surveyanswer.domain.repository;

import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyAnswerRepository{

    SurveyAnswer save(SurveyAnswer surveyAnswer);

    List<SurveyAnswer> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsBySurveyIdAndUserId(Long surveyId, Long userId);

    Long countBySurveyId(Long surveyId);

}
