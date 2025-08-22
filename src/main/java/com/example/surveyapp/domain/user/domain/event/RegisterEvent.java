package com.example.surveyapp.domain.user.domain.event;

import lombok.Getter;

@Getter
public class RegisterEvent {
    private final Long userId;

    public RegisterEvent(Long userId){
        this.userId = userId;
    }
}
