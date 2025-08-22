package com.example.surveyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class SurveyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurveyAppApplication.class, args);
    }

}
