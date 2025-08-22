package com.example.surveyapp.domain.survey.infrastructure;

import com.example.surveyapp.domain.survey.domain.model.enums.SurveyOutboxEnum;
import com.example.surveyapp.domain.survey.domain.repository.SurveyOutboxRepository;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class SurveyOutboxRepositoryImpl implements SurveyOutboxRepository {

    private final SurveyOutboxJpaRepository surveyOutboxJpaRepository;

    public SurveyOutbox save(SurveyOutbox surveyOutbox){
        return surveyOutboxJpaRepository.save(surveyOutbox);
    }

    @Override
    public List<SurveyOutbox> findByStatusAndPublished(SurveyOutboxEnum status, boolean published) {
        return surveyOutboxJpaRepository.findByStatusAndPublished(status,published);
    }
}
