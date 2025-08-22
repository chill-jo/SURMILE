package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.application.dto.OptionDto;
import com.example.surveyapp.domain.survey.application.dto.QuestionIdAndTypeDto;
import com.example.surveyapp.domain.survey.application.dto.QuestionDto;
import com.example.surveyapp.domain.survey.application.dto.SurveyInfoDto;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.domain.survey.exception.SurveyErrorCode;
import com.example.surveyapp.domain.survey.exception.SurveyException;
import com.example.surveyapp.domain.surveyanswer.application.facade.SurveyFacade;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SurveyFacadeImpl implements SurveyFacade {

    private final SurveyRepository surveyRepository;
    private final SurveyValidator surveyValidator = new SurveyValidator();
    private final SurveyQuestionService surveyQuestionService = new SurveyQuestionService();

    public Survey findSurvey(Long surveyId) {
        return surveyRepository.findByIdAndIsDeletedFalse(surveyId);
    }

    public Survey findByIdAndIsDeletedFalseWithPessimisticLock(Long surveyId){
        return surveyRepository.findByIdAndIsDeletedFalseWithPessimisticLock(surveyId);
    }

    public void validateAndReserveSlot(Long surveyId,Long count) {
        Survey survey = findByIdAndIsDeletedFalseWithPessimisticLock(surveyId);

        Long max = survey.getSurveyInfo().getMaxSurveyee();

        if (count >= max) {
            throw new SurveyException(SurveyErrorCode.SURVEY_NOT_IN_PROGRESS);
        }
    }

    @Override
    public void validateSurveyStartable(Long surveyId) {
        Survey survey = findByIdAndIsDeletedFalseWithPessimisticLock(surveyId);
        surveyValidator.validateStartable(survey);
    }

    @Override
    public QuestionIdAndTypeDto getQuestionIdAndTypeByNumber(Long surveyId, Long number){
        Survey survey = findSurvey(surveyId);
        Question question = surveyQuestionService.getQuestionByNumber(survey, number);
        return QuestionIdAndTypeDto.of(question.getId(), question.getType());
    }

    @Override
    public Long getPointPerPersonBySurveyId(Long surveyId){
        return findSurvey(surveyId).getSurveyInfo().getPointPerPerson().getValue();
    }

    @Override
    public SurveyInfoDto getSurveyInfo(Long surveyId){
        Survey survey = findSurvey(surveyId);
        return SurveyInfoDto.from(surveyId, survey.getSurveyInfo());
    }

    @Override
    public List<QuestionDto> getQuestionDtos(Long surveyId) {
        Survey survey = findSurvey(surveyId);
        List<Question> questions = surveyQuestionService.getQuestionsSortedByNumber(survey);

        return questions.stream()
                .map(QuestionDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<OptionDto> getOptionDtos(Long surveyId, Long questionId) {
        List<Options> options = surveyQuestionService.getQuestionById(findSurvey(surveyId), questionId).getOptionsOrderByNumber();

        return options.stream()
                .map(OptionDto::from)
                .collect(Collectors.toList());
    }
}
