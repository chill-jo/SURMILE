package com.example.surveyapp.domain.survey.service;

import com.example.surveyapp.domain.ai.moderation.config.ModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.service.ModerationService;
import com.example.surveyapp.domain.survey.controller.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.PageQuestionResponseDto;
import com.example.surveyapp.domain.survey.controller.dto.response.PageSurveyResponseDto;
import com.example.surveyapp.domain.survey.controller.dto.response.QuestionResponseDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.survey.domain.repository.OptionsRepository;
import com.example.surveyapp.domain.survey.domain.repository.QuestionRepository;
import com.example.surveyapp.domain.survey.domain.repository.SurveyRepository;
import com.example.surveyapp.domain.survey.facade.UserFacade;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SurveyRepository surveyRepository;
    private final UserFacade userFacade;
    private final OptionsRepository optionsRepository;
    private final ModerationService moderationService;

    @Transactional
    public QuestionResponseDto createQuestion(Long userId, Long surveyId, QuestionCreateRequestDto requestDto){

        User user = userFacade.findUser(userId);
        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        isSurveyNotStarted(survey);
        validateQuestionModeration(requestDto.getContent());

        Question question = new Question(
                survey,
                requestDto.getNumber(),
                requestDto.getContent(),
                requestDto.getType()
        );

        questionRepository.save(question);

        return new QuestionResponseDto(question.getId(), question.getNumber(), question.getContent(), question.getType());
    }

    //질문단건조회 (필요한가??)
    //(inprogress일때는 모두, 다른 상태에는 관리자랑 출제자만 조회가능) - 구현됨
    //이미 참여한 설문인지 확인해야함. - 응답 도메인 생성 후 수정
    public QuestionResponseDto getQuestion(Long userId, Long surveyId, Long questionId){

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        isQuestionFromSurvey(survey, question);

        if(!survey.isInProgress()){
            isCurrentUserSurveyee(user);
            currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        }

        return new QuestionResponseDto(question.getId(), question.getNumber(), question.getContent(), question.getType());
    }

    //질문목록조회
    //(inprogress일때는 모두, 다른 상태에는 관리자랑 출제자만 조회가능)- 구현됨,
    //in progress이고 유저가 참여자권한인 경우 이미 참여했는지 확인해야함. - 응답 도메인 생성 후 수정
    public PageQuestionResponseDto<QuestionResponseDto> getQuestions(int page, int size, Long userId, Long surveyId){

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        if(!survey.isInProgress()){
            isCurrentUserSurveyee(user);
            currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        }

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

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        isSurveyNotStarted(survey);
        isQuestionFromSurvey(survey, question);
        validateQuestionModeration(requestDto.getContent());

        if(requestDto.getNumber() != null){
            question.changeNumber(requestDto.getNumber());
        }
        if(requestDto.getContent() != null){
            question.changeContent(requestDto.getContent());
        }
        if(requestDto.getType() != null){
            question.changeQuestionType(requestDto.getType());
        }

        if(question.isSubjective()){
            optionsRepository.deleteAllByQuestion(question);
        }

        return new QuestionResponseDto(question.getId(), question.getNumber(), question.getContent(), question.getType());

    }

    @Transactional
    public void deleteQuestion(Long userId, Long surveyId, Long questionId){

        User user = userFacade.findUser(userId);

        Survey survey = surveyRepository.findByIdAndIsDeletedFalse(surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        currentUserMatchesSurveyCreatorOrAdmin(user, survey);
        isSurveyNotStarted(survey);
        isQuestionFromSurvey(survey, question);

        optionsRepository.deleteAllByQuestion(question);

        questionRepository.delete(question);
    }

    //참여자 권한 막기(생성, 수정, 삭제, 진행중아닌 설문선택지조회)
    public void isCurrentUserSurveyee(User user){

        if(user.isUserRoleSurveyee()){
            throw new CustomException(ErrorCode.SURVEYEE_NOT_ALLOWED);
        }
    }

    //해당 설문 출제자가 아니거나 관리자가 아닐 시 예외
    public void currentUserMatchesSurveyCreatorOrAdmin(User user, Survey survey){

        if(!survey.isUserSurveyCreator(user) && user.isUserRoleNotAdmin()){
            throw new CustomException(ErrorCode.NOT_SURVEY_CREATOR);
        }
    }

    //설문 상태가 진행 전이 아닐 때 생성, 수정, 삭제 시 예외
    public void isSurveyNotStarted(Survey survey){
        if(!survey.isNotStarted()){
            throw new CustomException(ErrorCode.SURVEY_STARTED);
        }
    }

    //설문에 포함된 질문이 아닐 때 예외
    public void isQuestionFromSurvey(Survey survey, Question question){
        if(!question.isFromSurvey(survey)){
            throw new CustomException(ErrorCode.QUESTION_NOT_FROM_SURVEY);
        }
    }

    // 질문 내용 적절성 검사
    private void validateQuestionModeration(String content) {
        ModerationResultStatusEnum titleStatus = moderationService.moderate("content", content);

        if(titleStatus == ModerationResultStatusEnum.DENIED){
            throw new CustomException(ErrorCode.INVALID_QUESTION_CONTENT);
        }
    }
}
