package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.AnserEnum;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswerOutbox;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class SurveyAnswerOutboxRepositoryImpl implements SurveyAnswerOutboxRepository {

    private final SurveyAnswerOutboxJpaRepository surveyAnswerOutboxJpaRepository;

    public SurveyAnswerOutbox save(SurveyAnswerOutbox surveyAnswerOutbox){
        return surveyAnswerOutboxJpaRepository.save(surveyAnswerOutbox);
    }

    @Override
    public List<SurveyAnswerOutbox> findByStatusAndPublished(AnserEnum status, boolean published) {
        return surveyAnswerOutboxJpaRepository.findByStatusAndPublished(status, published);
    }
}
