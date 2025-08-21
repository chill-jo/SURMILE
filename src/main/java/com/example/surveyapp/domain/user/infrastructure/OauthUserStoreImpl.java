package com.example.surveyapp.domain.user.infrastructure;

import com.example.surveyapp.domain.user.domain.model.AuthProvider;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import com.example.surveyapp.global.store.OauthUserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthUserStoreImpl implements OauthUserStore {

    private final UserRepository userRepository;

    @Override
    public User linkGoogleIfNeeded(Long userId) {
        User user1 = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));



        return user1;
    }

    @Override
    public User createsocialUser(String email, String name, AuthProvider provider, String providerId) {
        return null;
    }
}
