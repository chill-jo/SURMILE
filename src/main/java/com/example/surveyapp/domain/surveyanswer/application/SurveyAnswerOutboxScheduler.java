package com.example.surveyapp.domain.surveyanswer.application;

import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.AnserEnum;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswerOutbox;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerOutboxRepository;
import com.example.surveyapp.domain.surveyanswer.exception.AnswerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyAnswerOutboxScheduler {

    private final ObjectMapper objectMapper;
    private final SurveyAnswerOutboxRepository surveyAnswerOutboxRepository;
    private final AnswerEventPublisher eventPublisher;

    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishOutboxEvents() throws Exception {
    List<SurveyAnswerOutbox> unpublished = surveyAnswerOutboxRepository.findByStatusAndPublished(AnserEnum.FAILED, false);

    for (SurveyAnswerOutbox answerOutbox : unpublished) {
        try {
            eventPublisher.answerPublishEvent(objectMapper.readValue(answerOutbox.getPayload(),SurveyAnswerEvent.class));
            answerOutbox.markPublished();
        }catch (AnswerException e) {
            answerOutbox.markFailed(MAX_RETRY);
        }
        surveyAnswerOutboxRepository.save(answerOutbox);
    }

    }
}
