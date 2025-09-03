package com.example.surveyapp.domain.order.infrastructure;

import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Page<Order> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable) {

        return orderJpaRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
    }

    @Override
    public Optional<Order> findByIdAndIsDeletedFalse(Long id) {

        return orderJpaRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderJpaRepository.findAll(pageable);
    }
}
