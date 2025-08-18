package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.domain.repository.QuestionReadRepository;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.PageQuestionResponseDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.QuestionResponseDto;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.domain.survey.infrastructure.QuestionReadEntity;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionReadRepository questionReadRepository;
    private final SurveyQueryService surveyQueryService;
    private final SurveyValidator surveyValidator = new SurveyValidator();
    private final SurveyQuestionService surveyQuestionService = new SurveyQuestionService();
    private final UserReader userReader;

    @Transactional
    public QuestionResponseDto createQuestion(Long userId, Long surveyId, QuestionCreateRequestDto requestDto){

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQueryService.findSurvey(surveyId);
        surveyValidator.validateUpdatable(userId, survey);

        Question question = Question.from(requestDto, surveyId);

        surveyQuestionService.addQuestion(survey, question);

        return new QuestionResponseDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType()
        );
    }

    public QuestionResponseDto getQuestion(Long userId, Long surveyId, Long questionId){

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQueryService.findSurvey(surveyId);
        surveyValidator.validateQuestionAccess(userId, survey, userReader.validateUserRoleToSurveyee(userId));

        Question question = surveyQuestionService.getQuestionById(survey, questionId);

        return new QuestionResponseDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType()
        );
    }

    public PageQuestionResponseDto<QuestionResponseDto> getQuestions(int page, int size, Long userId, Long surveyId){

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQueryService.findSurvey(surveyId);
        surveyValidator.validateQuestionAccess(userId, survey, userReader.validateUserRoleToSurveyee(userId));

        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionReadEntity> questionReadEntityPage = questionReadRepository.findAllBySurveyId(surveyId, pageable);
        Page<Question> questionPage = questionReadEntityPage.map(QuestionReadEntity::toQuestion);

        Page<QuestionResponseDto> questionResponseDtoPage = questionPage.map(question -> new QuestionResponseDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType()
        ));

        return new PageQuestionResponseDto<>(questionResponseDtoPage);
    }

    @Transactional
    public QuestionResponseDto updateQuestion(Long userId, Long surveyId, Long questionId, QuestionUpdateRequestDto requestDto){

        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQueryService.findSurvey(surveyId);
        surveyValidator.validateUpdatable(userId, survey);

        Question question = surveyQuestionService.updateQuestion(
                survey,
                questionId,
                requestDto.getNumber(),
                requestDto.getContent(),
                requestDto.getType());

        return new QuestionResponseDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType()
        );
    }

    @Transactional
    public void deleteQuestion(Long userId, Long surveyId, Long questionId){

        userReader.validateUserIdOrThrow(userId);

        Survey survey = surveyQueryService.findSurvey(surveyId);
        surveyValidator.validateDeletable(userId, survey);

        surveyQuestionService.deleteQuestionById(survey, questionId);
    }
}
