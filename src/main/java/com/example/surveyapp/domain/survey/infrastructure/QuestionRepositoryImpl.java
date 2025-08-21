package com.example.surveyapp.domain.survey.infrastructure;

import com.example.surveyapp.domain.survey.domain.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {
    private final QuestionJpaRepository questionReadRepository;

    @Override
    public Page<QuestionReadEntity> findAllBySurveyId(Long surveyId, Pageable pageable) {
        return questionReadRepository.findAllBySurveyId(surveyId, pageable);
    }
}
