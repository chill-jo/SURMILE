package com.example.surveyapp.domain.surveyanswer.application;

import com.example.surveyapp.domain.survey.application.facade.SurveyAnswerFacade;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import com.example.surveyapp.domain.surveyanswer.exception.AnswerErrorCode;
import com.example.surveyapp.domain.surveyanswer.exception.AnswerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyAnswerQueryService implements SurveyAnswerFacade {

    private final SurveyAnswerRepository surveyAnswerRepository;

    public void validateParticipated(Long userId, Long surveyId){
        if(surveyAnswerRepository.existsBySurveyIdAndUserId(surveyId, userId)){
            throw new AnswerException(AnswerErrorCode.SURVEY_ALREADY_PARTICIPATED);
        }
    }

    @Override
    public Long getParticipatedCount(Long surveyId) {
        return surveyAnswerRepository.countBySurveyId(surveyId);
    }
}
