package com.example.surveyapp.domain.product.infrastructure;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Page<Product> findAllByStatusAndIsDeletedFalse(Status status, Pageable pageable) {
        return productJpaRepository.findAllByStatusAndIsDeletedFalse(status,pageable);
    }

    @Override
    public Optional<Product> findByIdAndStatusAndIsDeletedFalse(Long id, Status status) {
        return productJpaRepository.findByIdAndStatusAndIsDeletedFalse(id,status);
    }

    @Override
    public boolean existsByTitleAndIsDeletedFalse(String title) {
        return productJpaRepository.existsByTitleAndIsDeletedFalse(title);
    }

    @Override
    public Optional<Product> findByIdAndIsDeletedFalse(Long id) {
        return productJpaRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }
}
