package com.example.surveyapp.domain.order.infrastructor;

import com.example.surveyapp.domain.order.domain.model.OrderOutbox;
import com.example.surveyapp.domain.order.domain.model.vo.OrderOutBoxEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutbox, Long> {
    OrderOutbox save(OrderOutbox outbox);

    List<OrderOutbox> findByStatusAndPublished(OrderOutBoxEnum status, boolean published);

}
