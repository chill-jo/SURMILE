package com.example.surveyapp.domain.survey.infrastructure;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.exception.SurveyErrorCode;
import com.example.surveyapp.domain.survey.exception.SurveyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository {

    private final SurveyJpaRepository surveyJpaRepository;

    @Override
    public Survey save(Survey survey) {
        return surveyJpaRepository.save(survey);
    }

    @Override
    public Survey findByIdAndIsDeletedFalse(Long id) {
        return surveyJpaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }

    @Override
    public Survey findByIdAndIsDeletedFalseWithPessimisticLock(Long id) {
        return surveyJpaRepository.findByIdAndIsDeletedFalseWithPessimisticLock(id)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }

    @Override
    public Page<Survey> findAllSurveyPaged(Pageable pageable) {
        return surveyJpaRepository.findAllSurveyPaged(pageable);
    }

    @Override
    public Survey findSurveyWithQuestionsAndOptions(Long surveyId) {
        return surveyJpaRepository.findSurveyWithQuestionsAndOptions(surveyId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }
}
