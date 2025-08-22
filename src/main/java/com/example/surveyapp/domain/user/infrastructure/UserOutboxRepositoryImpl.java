package com.example.surveyapp.domain.user.infrastructure;

import com.example.surveyapp.domain.user.domain.model.UserOutbox;
import com.example.surveyapp.domain.user.domain.model.UserOutboxEnum;
import com.example.surveyapp.domain.user.domain.repository.UserOutboxRepository;
import com.example.surveyapp.domain.user.infrastructure.UserOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserOutboxRepositoryImpl implements UserOutboxRepository{

    private final UserOutboxJpaRepository userOutboxJpaRepository;

    public UserOutbox save(UserOutbox userOutbox){
        return userOutboxJpaRepository.save(userOutbox);
    }

    @Override
    public List<UserOutbox> findByStatusAndPublished(UserOutboxEnum status, boolean published) {
        return userOutboxJpaRepository.findByStatusAndPublished(status,published);
    }
}
