package com.example.surveyapp.domain.admin.infrastructure;

import com.example.surveyapp.domain.admin.domain.model.BlackList;
import com.example.surveyapp.domain.admin.domain.repository.BlackListRepository;
import com.example.surveyapp.domain.admin.exception.AdminErrorCode;
import com.example.surveyapp.domain.admin.exception.AdminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BlackListRepositoryImpl implements BlackListRepository {

    private final BlackListJpaRepository blackListJpaRepository;

    @Override
    public Optional<BlackList> findByUserId(Long userId) {
        return blackListJpaRepository.findByUserId(userId);

    }

    @Override
    public boolean existsByUserId(Long userId) {
        return blackListJpaRepository.existsByUserId(userId);
    }

    @Override
    public BlackList save(BlackList blackList){
        return blackListJpaRepository.save(blackList);
    }

    @Override
    public void delete(BlackList blackList){
        blackListJpaRepository.delete(blackList);
    }
}
