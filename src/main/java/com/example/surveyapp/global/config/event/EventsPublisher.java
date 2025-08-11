package com.example.surveyapp.global.config.event;

import org.springframework.context.ApplicationEventPublisher;

public class EventsPublisher {

    public static ApplicationEventPublisher eventPublisher;

    public static void raise(Object evnet) {
        if(evnet != null) {
            eventPublisher.publishEvent(evnet);
        }
    }
}
