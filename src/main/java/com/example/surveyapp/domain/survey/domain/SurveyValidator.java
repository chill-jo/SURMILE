package com.example.surveyapp.domain.survey.domain;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.reader.UserReader;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SurveyValidator {
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final UserReader userReader;

    public void validateStartable(Survey survey){
        if(!survey.isInProgress()){
            throw new CustomException(ErrorCode.SURVEY_NOT_IN_PROGRESS);
        }

        if(survey.getSurveyInfo().getDeadline().isBefore(LocalDateTime.now())){
            throw new CustomException(ErrorCode.SURVEY_NOT_IN_PROGRESS);
        }
    }

    public void validateNotParticipated(Long userId, Long surveyId){
        if (surveyAnswerRepository.existsBySurveyIdAndUserId(surveyId, userId)) {
            throw new CustomException(ErrorCode.SURVEY_ALREADY_PARTICIPATED);
        }
    }

    public void validateUpdatable(Long userId, Survey survey){
        if(!survey.isNotStarted()){
            throw new CustomException(ErrorCode.SURVEY_STARTED);
        }
        if(!survey.isUserSurveyCreator(userId)){
            throw new CustomException(ErrorCode.NOT_SURVEY_CREATOR);
        }
    }

    public void validateStatusUpdatable(Long userId, Survey survey){
        if(!survey.isUserSurveyCreator(userId)){
            throw new CustomException(ErrorCode.NOT_SURVEY_CREATOR);
        }
    }

    public void validateDeletable(Long userId, Survey survey){
        if(!survey.isUserSurveyCreator(userId)){
            throw new CustomException(ErrorCode.NOT_SURVEY_CREATOR);
        }
        if(survey.isInProgress()){
            throw new CustomException(ErrorCode.SURVEY_CANNOT_BE_DELETED);
        }
    }

    public void validateQuestionAccess(Long userId, Survey survey){
        if(!survey.isInProgress() && !survey.isUserSurveyCreator(userId)){
            throw new CustomException(ErrorCode.NOT_SURVEY_CREATOR);
        }
        if(!survey.isInProgress() && userReader.validateUserRole(userId, UserRoleEnum.SURVEYEE)){
            throw new CustomException(ErrorCode.SURVEYEE_NOT_ALLOWED);
        }
    }
}
