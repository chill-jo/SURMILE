package com.example.surveyapp.domain.order.infrastructure;

import com.example.surveyapp.domain.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    Optional<Order> findByIdAndIsDeletedFalse(Long id);

    Page<Order> findAll(Pageable pageable);
}
