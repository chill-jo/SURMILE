package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyOptionsAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyOptionsAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyOptionsAnswerRepositoryImpl implements SurveyOptionsAnswerRepository {

    private final SurveyOptionsAnswerJpaRepository surveyOptionsAnswerJpaRepository;

    @Override
    public Long countByQuestionIdAndNumber(Long questionId, Long number) {
        return surveyOptionsAnswerJpaRepository.countByQuestionIdAndNumber(questionId, number);
    }

    @Override
    public SurveyOptionsAnswer save(SurveyOptionsAnswer surveyOptionsAnswer) {
        return surveyOptionsAnswerJpaRepository.save(surveyOptionsAnswer);
    }
}
