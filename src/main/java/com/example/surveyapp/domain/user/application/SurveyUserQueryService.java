package com.example.surveyapp.domain.user.application;

import com.example.surveyapp.domain.survey.application.facade.UserFacade;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyUserQueryService implements UserFacade {
    private final UserRepository userRepository;

    @Override
    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));
    }
}
