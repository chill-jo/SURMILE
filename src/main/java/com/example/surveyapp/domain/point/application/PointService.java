package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.example.surveyapp.domain.point.presentation.dto.response.PointHistoryResponseDto;
import com.example.surveyapp.domain.point.domain.model.entity.*;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void createPointWallet(Long userId){

        if(!pointRepository.existsByUserId(userId)){
            PointWallet pointWallet = PointWallet.of(userId);
            pointRepository.save(pointWallet);
        }
    }

    @Transactional(readOnly = true)
    public Page<PointHistoryResponseDto> getHistories(Long userId, Pageable pageable){

        return pointHistoryRepository.findPointHistoryByUserId(userId, pageable)
                .map(PointHistoryResponseDto::from);
    }

    /**
     * 공통되는 로직을 메서드로 분리
     * 해당하는 userId로 포인트를 조회한다.
     * 조회하지 못하는 경우 CustomException 발생
     * @param userId
     * @return
     */
    private PointWallet getPoint(Long userId){
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
    }

}
