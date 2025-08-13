package com.example.surveyapp.domain.admin.application.facade;

import com.example.surveyapp.domain.admin.presentation.dto.StatsListDto;
import com.example.surveyapp.domain.admin.presentation.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserFacade {
    UserDto getUserDto(Long userId);

    Page<UserDto> findAllBySearch(String search, Pageable pageable);

    List<StatsListDto> getUserBaseDataStatistics(LocalDateTime startDate, LocalDateTime endDate);
}
