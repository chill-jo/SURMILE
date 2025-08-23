package com.example.surveyapp.domain.surveyanswer.application;

import com.example.surveyapp.domain.survey.application.dto.QuestionIdAndTypeDto;
import com.example.surveyapp.domain.surveyanswer.application.facade.SurveyFacade;
import com.example.surveyapp.domain.surveyanswer.application.mapper.SurveyAnswerMapper;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyDoneEvent;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswerOutbox;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerOutboxRepository;
import com.example.surveyapp.domain.surveyanswer.exception.AnswerErrorCode;
import com.example.surveyapp.domain.surveyanswer.exception.AnswerException;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.request.SurveyAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyeeSurveyListDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyAnswerRepository;
import com.example.surveyapp.domain.surveyanswer.domain.strategy.SurveyQuestionStrategy;
import com.example.surveyapp.global.aop.LockAnnotation;
import com.example.surveyapp.global.reader.UserReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyAnswerService {

    private final UserReader userReader;
    private final SurveyFacade surveyFacade;
    private final ApplicationEventPublisher eventPublisher;
    private final List<SurveyQuestionStrategy> surveyQuestionStrategies;
    private final SurveyAnswerMapper surveyAnswerFactory;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyAnswerQueryService surveyAnswerQueryService;
    private final ObjectMapper objectMapper;
    private final SurveyAnswerOutboxRepository surveyAnswerOutboxRepository;

    @Transactional
    @LockAnnotation(key = "'survey:' + #surveyId")
    public void saveSurveyAnswer(Long surveyId, SurveyAnswerRequestDto requestDto, Long userId) {

        userReader.validateUserIdOrThrow(userId);
        surveyFacade.validateAndReserveSlot(surveyId, surveyAnswerRepository.countBySurveyId(surveyId));
        surveyFacade.validateSurveyStartable(surveyId);
        surveyAnswerQueryService.validateParticipated(userId, surveyId);

        SurveyAnswer surveyAnswer = surveyAnswerRepository.save(SurveyAnswer.of(surveyId, userId));

        saveAnswerWithStrategy(requestDto, surveyId, surveyAnswer);

        eventPublisher.publishEvent(new SurveyAnswerEvent(
                userId,
                surveyFacade.getPointPerPersonBySurveyId(surveyId),
                surveyAnswer.getId()
        ));

        SurveyAnswerEvent event = new SurveyAnswerEvent(userId,
                surveyFacade.getPointPerPersonBySurveyId(surveyId),
                surveyAnswer.getId());
        try {
            String payload = objectMapper.writeValueAsString(event);
            SurveyAnswerOutbox answerOutbox = SurveyAnswerOutbox.of("Survey_Answer",
                    surveyAnswer.getId(),
                    payload
                    );

            surveyAnswerOutboxRepository.save(answerOutbox);

        } catch (JsonProcessingException e) {
            throw new AnswerException(AnswerErrorCode.CANNOT_CONVERT_PAYLOAD);
        }

        if (surveyAnswerRepository.countBySurveyId(surveyId) >= surveyFacade.getSurveyInfo(surveyId).getMaxSurveyee()) {
            eventPublisher.publishEvent(new SurveyDoneEvent(surveyId));
        }
    }

    public void saveAnswerWithStrategy(SurveyAnswerRequestDto requestDto, Long surveyId, SurveyAnswer surveyAnswer) {
        requestDto.getAnswers().forEach(questionAnswer -> {
            QuestionIdAndTypeDto questionDto = surveyFacade.getQuestionIdAndTypeByNumber(surveyId, questionAnswer.getNumber());

            // TODO 질문 타입에 따라 각 질문 응답을 처리할 수 있는 전략을 별도로 구성해서 처리하면 어떨까
            surveyQuestionStrategies.stream()
                    .filter(it -> it.isSupport(questionDto.getQuestionType()))
                    .findFirst()
                    .orElseThrow()
                    .doSave(questionAnswer, surveyAnswer, questionDto.getQuestionId());
        });
    }

    @Transactional(readOnly = true)
    public SurveyeeSurveyListDto getUserSurveyAnswerHistory(Long userId) {

        userReader.validateUserIdOrThrow(userId);
        List<SurveyAnswer> surveyAnswerList = surveyAnswerRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return surveyAnswerFactory.createParticipatedSurveyListDto(surveyAnswerList);
    }
}
