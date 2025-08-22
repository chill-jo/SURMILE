package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.AnserEnum;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswerOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyAnswerOutboxJpaRepository extends JpaRepository<SurveyAnswerOutbox, Long>{
    List<SurveyAnswerOutbox> findByStatusAndPublished(AnserEnum status, boolean published);

}
