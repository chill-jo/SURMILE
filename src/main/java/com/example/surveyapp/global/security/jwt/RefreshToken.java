package com.example.surveyapp.global.security.jwt;

import lombok.Getter;

@Getter
public class RefreshToken {

    private String refreshToken;

    private boolean isExpired;

}
