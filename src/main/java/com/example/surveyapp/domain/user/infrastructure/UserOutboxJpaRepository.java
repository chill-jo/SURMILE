package com.example.surveyapp.domain.user.infrastructure;

import com.example.surveyapp.domain.user.domain.model.UserOutboxEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.surveyapp.domain.user.domain.model.UserOutbox;

import java.util.List;

public interface UserOutboxJpaRepository extends JpaRepository<UserOutbox, Long> {
    List<UserOutbox> findByStatusAndPublished(UserOutboxEnum status, boolean published);

}
