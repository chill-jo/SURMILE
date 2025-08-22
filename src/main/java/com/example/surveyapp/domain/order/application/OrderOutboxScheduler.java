package com.example.surveyapp.domain.order.application;

import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
import com.example.surveyapp.domain.order.domain.model.OrderOutbox;
import com.example.surveyapp.domain.order.domain.model.vo.OrderOutBoxEnum;
import com.example.surveyapp.domain.order.domain.repository.OrderOutboxRepository;
import com.example.surveyapp.domain.order.exception.OrderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderOutboxScheduler {

    private final ObjectMapper objectMapper;
    private final OrderOutboxRepository orderOutboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishOutboxEvents() throws Exception {
         List<OrderOutbox> unpublished = orderOutboxRepository.findByStatusAndPublished(OrderOutBoxEnum.READY, false);

         for (OrderOutbox orderOutbox : unpublished) {
             try {
                 eventPublisher.publishEvent(objectMapper.readValue(orderOutbox.getPayload(), OrderCreateEvent.class));

                 orderOutbox.markPublished();
                 orderOutboxRepository.save(orderOutbox);
             }catch (OrderException e) {
                orderOutbox.markFailed(MAX_RETRY);

                orderOutboxRepository.save(orderOutbox);
             }
         }
    }
}
