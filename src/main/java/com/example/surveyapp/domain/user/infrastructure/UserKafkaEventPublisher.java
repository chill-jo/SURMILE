package com.example.surveyapp.domain.user.infrastructure;

import com.example.surveyapp.domain.user.application.UserEventPublisher;
import com.example.surveyapp.domain.user.domain.event.RegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserKafkaEventPublisher implements UserEventPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    public void publishEvent(RegisterEvent event) {
    kafkaTemplate.send("wallet-created",event);
    }
}
