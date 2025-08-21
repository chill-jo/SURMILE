package com.example.surveyapp.domain.user.infrastructure;

import com.example.surveyapp.domain.admin.domain.repository.BlackListRepository;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import com.example.surveyapp.global.oauth.reader.OauthReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReaderImpl implements OauthReader {
    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;

    @Override
    public void validateUserIdOrThrow(Long userId) {
        if(!userRepository.existsByIdAndIsDeletedFalse(userId)){
            throw new UserException(UserErrorCode.NOT_FOUND_USER);
        }
        if(blackListRepository.existsByUserId(userId)){
            throw new UserException(UserErrorCode.IS_BLACKLIST);
        }
    }


    @Override
    public String usernameById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));
        return user.getName();
    }

    @Override
    public boolean validateUserRoleToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));
        return user.hasRole(UserRoleEnum.ADMIN);
    }

    @Override
    public boolean validateUserRoleToSurveyee(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));
        return user.hasRole(UserRoleEnum.SURVEYEE);
    }

    @Override
    public boolean validateUserRoleToSurveyor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));
        return user.hasRole(UserRoleEnum.SURVEYOR);
    }
}