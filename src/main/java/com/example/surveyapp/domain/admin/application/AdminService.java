package com.example.surveyapp.domain.admin.application;

import com.example.surveyapp.domain.admin.application.facade.UserFacade;
import com.example.surveyapp.domain.admin.exception.AdminErrorCode;
import com.example.surveyapp.domain.admin.exception.AdminException;
import com.example.surveyapp.domain.admin.presentation.dto.StatsListDto;
import com.example.surveyapp.domain.admin.presentation.dto.UserDto;
import com.example.surveyapp.domain.admin.domain.model.BlackList;
import com.example.surveyapp.domain.admin.domain.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserFacade userFacade;
    private final BlackListRepository blackListRepository;


    public Page<UserDto> getUserList(String search, Pageable pageable) {

        return userFacade.findAllBySearch(search, pageable);
    }


    public UserDto getUser(Long userId) {

        return userFacade.getUserDto(userId);
    }


    public List<StatsListDto> getStats(LocalDateTime startDateLocal, LocalDateTime endDateLocal) {

        return userFacade.getUserBaseDataStatistics(startDateLocal, endDateLocal);
    }

    @Transactional
    public UserDto addBlackList(Long userId) {

        UserDto userDto = userFacade.getUserDto(userId);

        if (blackListRepository.findByUserId(userId).isPresent()) {
            throw new AdminException(AdminErrorCode.IS_BLACKLIST);
        }

        blackListRepository.save(BlackList.of(userId));

        return userDto;
    }


    @Transactional
    public UserDto deleteBlackList(Long userId) {

        UserDto userDto = userFacade.getUserDto(userId);

        BlackList blackList = blackListRepository.findByUserId(userId).orElseThrow(
                () -> new AdminException(AdminErrorCode.IS_NOT_BLACKLIST)
        );

        blackListRepository.delete(blackList);

        return userDto;
    }

}
