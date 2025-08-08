package com.example.surveyapp.domain.survey.service;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.PageQuestionResponseDto;
import com.example.surveyapp.domain.survey.controller.dto.response.QuestionResponseDto;
import com.example.surveyapp.domain.survey.domain.SurveyQuestionQueryService;
import com.example.surveyapp.domain.survey.domain.SurveyValidator;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.repository.QuestionRepository;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
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

    private final QuestionRepository questionRepository;
    private final UserReader userReader;
    private final SurveyValidator surveyValidator;
    private final SurveyQuestionQueryService surveyQuestionQueryService;
    private final SurveyRepository surveyRepository;

    @Transactional
    public QuestionResponseDto createQuestion(Long userId, Long surveyId, QuestionCreateRequestDto requestDto){

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateUpdatable(userId, survey);

        Question question = Question.from(requestDto);

        survey.addQuestion(question);
        surveyRepository.save(survey);

        return new QuestionResponseDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType()
        );
    }

    //질문단건조회 (필요한가??)
    //(inprogress일때는 모두, 다른 상태에는 관리자랑 출제자만 조회가능) - 구현됨
    //이미 참여한 설문인지 확인해야함. - 응답 도메인 생성 후 수정
    public QuestionResponseDto getQuestion(Long userId, Long surveyId, Long questionId){

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateQuestionAccess(userId, survey);

        Question question = survey.getQuestionById(questionId);

        return new QuestionResponseDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType()
        );
    }

    //질문목록조회
    //(inprogress일때는 모두, 다른 상태에는 관리자랑 출제자만 조회가능)- 구현됨,
    //in progress이고 유저가 참여자권한인 경우 이미 참여했는지 확인해야함. - 응답 도메인 생성 후 수정
    public PageQuestionResponseDto<QuestionResponseDto> getQuestions(int page, int size, Long userId, Long surveyId){

        userReader.validateUserIdOrThrow(userId);
        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateQuestionAccess(userId, survey);

        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findAllBySurveyId(surveyId, pageable);

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

        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateUpdatable(userId,survey);

        Question question = survey.updateQuestion(
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

        Survey survey = surveyQuestionQueryService.findSurvey(surveyId);
        surveyValidator.validateDeletable(userId, survey);

        Question question = survey.getQuestionById(questionId);

        questionRepository.delete(question);
    }
}
