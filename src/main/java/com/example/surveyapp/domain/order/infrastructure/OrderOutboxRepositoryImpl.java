package com.example.surveyapp.domain.order.infrastructure;


import com.example.surveyapp.domain.order.domain.model.OrderOutbox;
import com.example.surveyapp.domain.order.domain.model.vo.OrderOutBoxEnum;
import com.example.surveyapp.domain.order.domain.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;

    @Override
    public OrderOutbox save(OrderOutbox orderOutbox) {
        return orderOutboxJpaRepository.save(orderOutbox);
    }

    @Override
    public List<OrderOutbox> findByStatusAndPublished(OrderOutBoxEnum status, boolean published) {
        return orderOutboxJpaRepository.findByStatusAndPublished(status,published);
    }
}
