package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveyAnswerRepositoryImpl implements SurveyAnswerRepository {

    private final SurveyAnswerJpaRepository surveyAnswerJpaRepository;

    @Override
    public SurveyAnswer save(SurveyAnswer surveyAnswer) {
        return surveyAnswerJpaRepository.save(surveyAnswer);
    }

    @Override
    public List<SurveyAnswer> findAllByUserIdOrderByCreatedAtDesc(Long userId) {
        return surveyAnswerJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public boolean existsBySurveyIdAndUserId(Long surveyId, Long userId) {
        return surveyAnswerJpaRepository.existsBySurveyIdAndUserId(surveyId, userId);
    }

    @Override
    public Long countBySurveyId(Long surveyId) {
        return surveyAnswerJpaRepository.countBySurveyId(surveyId);
    }
}
