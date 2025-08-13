package com.example.surveyapp.global.infra;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String adminEmail = "admin@test123.com";
        String adminName = "admin";
        String adminNickname = "admin1";
        String adminPassword = passwordEncoder.encode("password1!");

        //이미 admin 계정이 있을 시
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User admin = User.createAdmin(adminEmail, adminName, adminNickname ,adminPassword);
        userRepository.save(admin);
    }

}