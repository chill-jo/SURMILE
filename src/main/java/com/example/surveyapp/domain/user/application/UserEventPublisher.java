package com.example.surveyapp.domain.user.application;

import com.example.surveyapp.domain.user.domain.event.RegisterEvent;

public interface UserEventPublisher {
    void publishEvent(RegisterEvent event);
}
