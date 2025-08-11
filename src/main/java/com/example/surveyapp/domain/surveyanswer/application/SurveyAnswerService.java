package com.example.surveyapp.domain.surveyanswer.application;

import com.example.surveyapp.domain.survey.application.SurveyQuestionQueryService;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.domain.surveyanswer.application.factory.SurveyAnswerFactory;
import com.example.surveyapp.domain.surveyanswer.event.SurveyAnswerEvent;
import com.example.surveyapp.domain.surveyanswer.controller.dto.request.SurveyAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.controller.dto.response.SurveyeeSurveyListDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import com.example.surveyapp.domain.surveyanswer.domain.strategy.SurveyQuestionStrategy;
import com.example.surveyapp.global.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyAnswerService {

    private final UserReader userReader;
    private final SurveyQuestionQueryService surveyQuestionQueryService;
    private final SurveyValidator surveyValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final List<SurveyQuestionStrategy> surveyQuestionStrategies;
    private final SurveyAnswerFactory surveyAnswerFactory;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyQuestionService surveyQuestionService;

    @Transactional
    public void saveSurveyAnswer(Long surveyId, SurveyAnswerRequestDto requestDto, Long userId) {

        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateStartable(survey);
        surveyValidator.validateNotParticipated(userId, surveyId);

        SurveyAnswer surveyAnswer = surveyAnswerRepository.save(SurveyAnswer.of(surveyId, userId));

        saveAnswerWithStrategy(requestDto, survey, surveyAnswer);

        eventPublisher.publishEvent(new SurveyAnswerEvent(userId, survey, surveyAnswer.getId()));

        /// ////이부분을 이벤트로 처리할수는 없나?
        if (surveyAnswerRepository.countBySurveyId(surveyId) >= survey.getSurveyInfo().getMaxSurveyee()) {
            survey.changeSurveyStatus(SurveyStatus.DONE);
        }
        /// ////
    }

    public void saveAnswerWithStrategy(SurveyAnswerRequestDto requestDto, Survey survey, SurveyAnswer surveyAnswer){
        requestDto.getAnswers().forEach(questionAnswer -> {
            Question question = surveyQuestionService.getQuestionByNumber(survey, questionAnswer.getNumber());

            // TODO 질문 타입에 따라 각 질문 응답을 처리할 수 있는 전략을 별도로 구성해서 처리하면 어떨까
            surveyQuestionStrategies.stream()
                    .filter(it -> it.isSupport(question.getType()))
                    .findFirst()
                    .orElseThrow()
                    .doSave(questionAnswer, surveyAnswer, question.getId());
        });
    }

    @Transactional(readOnly = true)
    public SurveyeeSurveyListDto getUserSurveyAnswerHistory(Long userId) {

        userReader.validateUserIdOrThrow(userId);
        List<SurveyAnswer> surveyAnswerList = surveyAnswerRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return surveyAnswerFactory.createParticipatedSurveyListDto(surveyAnswerList);
    }

}
