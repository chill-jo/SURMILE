package com.example.surveyapp.domain.point.infrastructure.KafkaListener;

import com.example.surveyapp.domain.point.application.eventhandler.UserRegisterEventHandler;
import com.example.surveyapp.domain.user.domain.event.RegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaUserEventListener {
    private final UserRegisterEventHandler userRegisterEventHandler;

    @KafkaListener(topics = "wallet-created", groupId = "point-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(RegisterEvent event){
        userRegisterEventHandler.handleUserRegisterEvent(event);
    }
}
