package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyOutbox;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyOutboxEnum;
import com.example.surveyapp.domain.survey.domain.repository.SurveyOutboxRepository;
import com.example.surveyapp.domain.survey.exception.SurveyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyOutboxScheduler {

    private final ObjectMapper objectMapper;
    private final SurveyOutboxRepository surveyOutboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishOutboxEvents() throws Exception {
       List<SurveyOutbox> unpublished = surveyOutboxRepository.findByStatusAndPublished(SurveyOutboxEnum.READY, false);

    for (SurveyOutbox surveyOutbox : unpublished) {
        try {
            eventPublisher.publishEvent(objectMapper.readValue(surveyOutbox.getPayload(), SurveyCreateEvent.class));

            surveyOutbox.markPublished();
        } catch (SurveyException e) {
            surveyOutbox.markFailed(MAX_RETRY);
        }
        surveyOutboxRepository.save(surveyOutbox);
    }

    }
}
