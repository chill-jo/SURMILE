package com.example.surveyapp.domain.order.domain.repository;

import com.example.surveyapp.domain.order.domain.model.OrderOutbox;
import com.example.surveyapp.domain.order.domain.model.vo.OrderOutBoxEnum;

import java.util.List;

public interface OrderOutboxRepository {

    OrderOutbox save(OrderOutbox outbox);

    List<OrderOutbox> findByStatusAndPublished(OrderOutBoxEnum status, boolean published);
}
