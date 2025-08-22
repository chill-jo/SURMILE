package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyTextAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyTextAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyTextAnswerRepositoryImpl implements SurveyTextAnswerRepository {

    private final SurveyTextAnswerJpaRepository surveyTextAnswerJpaRepository;


    @Override
    public SurveyTextAnswer save(SurveyTextAnswer surveyTextAnswer) {
        return surveyTextAnswerJpaRepository.save(surveyTextAnswer);
    }
}
