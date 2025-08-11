package com.example.surveyapp.domain.survey.domain.repository;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

    List<SurveyAnswer> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsBySurveyIdAndUserId(Long surveyId, Long userId);

    Long countBySurveyId(Long surveyId);

}
