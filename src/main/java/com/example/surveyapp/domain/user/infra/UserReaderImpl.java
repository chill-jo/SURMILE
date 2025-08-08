package com.example.surveyapp.domain.user.infra;

import com.example.surveyapp.domain.admin.domain.repository.BlackListRepository;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.reader.UserReader;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {
    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;

    @Override
    public void validateUserIdOrThrow(Long userId) {
        if(!userRepository.existsByIdAndIsDeletedFalse(userId)){
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        if(blackListRepository.existsByUserId(userId)){
            throw new CustomException(ErrorCode.IS_BLACKLIST);
        }
    }

    public boolean isSurveyor(Long userId){
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_USER)
                );

        return user.isUserRoleSurveyor();
    }

    @Override
    public String usernameById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        return user.getName();
    }
}