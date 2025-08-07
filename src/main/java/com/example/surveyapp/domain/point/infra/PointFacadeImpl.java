package com.example.surveyapp.domain.point.infra;

import com.example.surveyapp.domain.order.facade.PointFacade;
import com.example.surveyapp.domain.point.domain.model.entity.Point;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacadeImpl implements PointFacade {

    private final PointRepository pointRepository;

    @Override
    public Point findPoint(Long id) {

        Point point = pointRepository.findByUserId(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ENOUGH_POINT));
        return point;
    }

    @Override
    public void redeem(Long userId, Long price) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POINT));

        if (point.getPointBalance() < price) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
        }
        point.redeem(price);

    }
}
