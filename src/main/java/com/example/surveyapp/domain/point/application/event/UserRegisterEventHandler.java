package com.example.surveyapp.domain.point.application.event;

import com.example.surveyapp.domain.point.application.PointService;
import com.example.surveyapp.domain.user.domain.event.RegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserRegisterEventHandler {

    private final PointService pointService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegisterEvent(RegisterEvent registerEvent){
        pointService.createPointWallet(registerEvent.getUserId());
    }
}

