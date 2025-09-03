package com.example.surveyapp.domain.user.application;

import com.example.surveyapp.domain.user.domain.event.RegisterEvent;
import com.example.surveyapp.domain.user.domain.model.UserOutbox;
import com.example.surveyapp.domain.user.domain.model.UserOutboxEnum;
import com.example.surveyapp.domain.user.domain.repository.UserOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserOutboxScheduler {

    private final ObjectMapper objectMapper;
    private final UserOutboxRepository userOutboxRepository;
    private final UserEventPublisher eventPublisher;

    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishOutboxEvents() throws Exception {
        List<UserOutbox> unpublished = userOutboxRepository.findByStatusAndPublished(UserOutboxEnum.READY, false);

        for (UserOutbox userOutbox : unpublished) {
            try {
                log.info("지금 이벤트  시작해유ㅎㅎ");
                eventPublisher.publishEvent(objectMapper.readValue(userOutbox.getPayload(), RegisterEvent.class));
                userOutbox.markPublished();
                log.info("지금 이벤트  돌아유ㅎㅎ");


            }catch (Exception e) {
                log.info("지금 실패해서 터졌어유ㅠㅠ");

                userOutbox.markFailed(MAX_RETRY);
            }
            userOutboxRepository.save(userOutbox);
        }

    }

}
