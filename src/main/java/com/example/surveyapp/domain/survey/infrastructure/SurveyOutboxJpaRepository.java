package com.example.surveyapp.domain.survey.infrastructure;

import com.example.surveyapp.domain.survey.domain.model.entity.SurveyOutbox;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyOutboxEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyOutboxJpaRepository extends JpaRepository<SurveyOutbox, Long> {
    List<SurveyOutbox> findByStatusAndPublished(SurveyOutboxEnum status, boolean published);

}
