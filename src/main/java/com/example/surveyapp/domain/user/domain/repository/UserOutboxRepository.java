package com.example.surveyapp.domain.user.domain.repository;

import com.example.surveyapp.domain.user.domain.model.UserOutbox;
import com.example.surveyapp.domain.user.domain.model.UserOutboxEnum;

import java.util.List;

public interface UserOutboxRepository {

    UserOutbox save(UserOutbox userOutbox);

    List<UserOutbox> findByStatusAndPublished(UserOutboxEnum status, boolean published);
}
