package com.example.surveyapp.global.config.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class Event {
    private final LocalDateTime timestamp;


    public Event() {
        this.timestamp = LocalDateTime.now();
    }


}
