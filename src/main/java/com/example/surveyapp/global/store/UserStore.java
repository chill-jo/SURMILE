package com.example.surveyapp.global.store;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.AuthProvider;

public interface UserStore {
    User linkGoogleIfNeeded(User user, String sub);
    User createsocialUser(String email, String name, AuthProvider provider, String providerId);

    default User linkGoogleIfNeed(User user, String sub) {
        return linkGoogleIfNeeded(user, AuthProvider.GOOGLE, sub);
    }
    default User createGoogleUser(String email, String name, String sub) {
        return createsocialUser(email, name, AuthProvider.GOOGLE, sub);
    }
}
