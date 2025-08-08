package com.example.surveyapp.domain.product.domain.repository;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findAllByStatusAndIsDeletedFalse(Status status, Pageable pageable);

    Optional<Product> findByIdAndStatusAndIsDeletedFalse(Long id, Status status);

    //동일한 상품명 찾는 메서드
    boolean existsByTitleAndIsDeletedFalse(String title);

    Optional<Product> findByIdAndIsDeletedFalse(Long id);
}
