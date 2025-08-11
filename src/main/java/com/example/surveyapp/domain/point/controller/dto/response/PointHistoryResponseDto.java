package com.example.surveyapp.domain.point.controller.dto.response;

import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

public class PointHistoryResponseDto {
    private PointType pointType;
    private Long amount;
    private LocalDateTime date;

    private PointHistoryResponseDto(PointType pointType, Long amount, LocalDateTime date){
        this.pointType=pointType;
        this.amount=amount;
        this.date=date;
    }

    public static PointHistoryResponseDto from(PointHistory history){
        return new PointHistoryResponseDto(
                history.getType(),
                history.getAmount().getValue(),
                history.getCreatedAt()
        );
    }
}
