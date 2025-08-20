package com.example.surveyapp.domain.order.domain.repository;

import com.example.surveyapp.domain.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepository {
    Page<Order> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    Optional<Order> findByIdAndIsDeletedFalse(Long id);

    Page<Order> findAll(Pageable pageable);

    Order save(Order order);
}
