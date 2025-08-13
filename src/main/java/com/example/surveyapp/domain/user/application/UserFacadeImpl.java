package com.example.surveyapp.domain.user.application;

import com.example.surveyapp.domain.admin.application.facade.UserFacade;
import com.example.surveyapp.domain.admin.presentation.dto.StatDto;
import com.example.surveyapp.domain.admin.presentation.dto.StatsListDto;
import com.example.surveyapp.domain.admin.presentation.dto.UserDto;
import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserBaseDataRepository;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserRepository userRepository;
    private final UserBaseDataRepository userBaseDataRepository;

    @Override
    public UserDto getUserDto(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(
                () -> new UserException(UserErrorCode.NOT_FOUND_USER)
        );

        return UserDto.from(user);
    }

    @Override
    public Page<UserDto> findAllBySearch(String search, Pageable pageable) {
        return userRepository.findAllBySearch(search, pageable);
    }

    @Override
    public List<StatsListDto> getUserBaseDataStatistics(LocalDateTime startDateLocal, LocalDateTime endDateLocal){

        List<CategoryEnum> categoryEnumList = Arrays.stream(CategoryEnum.values()).toList();

        return categoryEnumList.stream().map(
                categoryEnum -> {
                    StatsListDto statsListDto = StatsListDto.of(categoryEnum.getCategory());
                    for (Long i = 1L; i <= categoryEnum.getOptionMaxNum(); i++) {
                        Long count = userBaseDataRepository.countByCategoryAndDataAndStartDateAndEndDate(categoryEnum, i, startDateLocal, endDateLocal);
                        StatDto statDto = StatDto.of(i, count);
                        statsListDto.addStat(statDto);
                    }
                    return statsListDto;
                }
        ).toList();
    }
}
