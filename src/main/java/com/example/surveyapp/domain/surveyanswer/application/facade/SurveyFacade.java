package com.example.surveyapp.domain.surveyanswer.application.facade;

import com.example.surveyapp.domain.survey.application.dto.OptionDto;
import com.example.surveyapp.domain.survey.application.dto.QuestionIdAndTypeDto;
import com.example.surveyapp.domain.survey.application.dto.QuestionDto;
import com.example.surveyapp.domain.survey.application.dto.SurveyInfoDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;


import java.util.List;

public interface SurveyFacade {
    void validateSurveyStartable(Long surveyId);
    QuestionIdAndTypeDto getQuestionIdAndTypeByNumber(Long surveyId, Long number);
    Long getPointPerPersonBySurveyId(Long surveyId);
    SurveyInfoDto getSurveyInfo(Long surveyId);
    List<QuestionDto> getQuestionDtos(Long surveyId);
    List<OptionDto> getOptionDtos(Long surveyId, Long questionId);
    Survey findSurveyWithPessimisticLock(Long surveyId);
    void validateAndReserveSlot(Long surveyId);
}
